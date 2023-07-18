package org.blbulyandavbulyan.blog.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.blbulyandavbulyan.blog.dtos.article.ArticleDto;
import org.blbulyandavbulyan.blog.dtos.article.ArticleForPublishing;
import org.blbulyandavbulyan.blog.dtos.article.ArticlePublished;
import org.blbulyandavbulyan.blog.dtos.authorization.JwtRequest;
import org.blbulyandavbulyan.blog.dtos.authorization.JwtResponse;
import org.blbulyandavbulyan.blog.entities.Role;
import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.services.ArticlesService;
import org.blbulyandavbulyan.blog.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public class ArticleControllerTest {
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private ArticlesService articlesService;
    @Autowired
    private ObjectMapper objectMapper;
    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .apply(springSecurity())
                .alwaysDo(document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
                .build();
        User david = new User();
        david.setUserId(1L);
        david.setName("david");
        david.setRoles(new ArrayList<>(List.of(new Role("ROLE_ADMIN"))));
        Mockito.when(userService.findByName("david")).thenReturn(david);
    }
    private void createArticle(ResultMatcher resultMatcher) throws Exception {
        ArticleForPublishing articleForPublishing = new ArticleForPublishing("Test title", "Test text");
        mockMvc.perform(post("/api/v1/articles").contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(articleForPublishing)))
                .andExpect(resultMatcher)
                .andDo(
                        document(
                                "create-article-example",
                                requestFields(
                                        fieldWithPath("title").description("The title of a new article"),
                                        fieldWithPath("text").description("The text of a new article")
                                )
                        )
                );
    }
    @Test
    @WithMockUser(username = "david", roles = {"PUBLISHER"})
    public void createArticle() throws Exception {
        createArticle(status().isCreated());
    }
    @Test
    @WithMockUser(username = "david", roles = {"COMMENTER"})
    public void createArticleWhenUserIsNotAPublisher() throws Exception {
        createArticle(status().isForbidden());
    }
    @Test
    @WithMockUser(username = "david", roles = {"COMMENTER", "PUBLISHER"})
    public void getArticleByIdIfArticleExists() throws Exception {
        ArticleDto articleDto = new ArticleDto("My test article", "Something very long", "david");
        Mockito.when(articlesService.getById(1L)).thenReturn(articleDto);
        mockMvc.perform(get("/api/v1/articles/{id}", 1L)).andExpect(status().isOk()).andExpect(result -> {
            ArticleDto receivedArticleDTO = objectMapper.readValue(result.getResponse().getContentAsString(), ArticleDto.class);
            assertThat(articleDto).isEqualTo(receivedArticleDTO);
        });
    }
}
