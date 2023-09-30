package org.blbulyandavbulyan.blog.services;

import org.blbulyandavbulyan.blog.dtos.article.ArticleInfoDTO;
import org.blbulyandavbulyan.blog.dtos.article.ArticlePublishedResponse;
import org.blbulyandavbulyan.blog.dtos.article.ArticleResponse;
import org.blbulyandavbulyan.blog.dtos.article.CreateArticleRequest;
import org.blbulyandavbulyan.blog.entities.Article;
import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.exceptions.articles.ArticleNotFoundException;
import org.blbulyandavbulyan.blog.exceptions.security.AccessDeniedException;
import org.blbulyandavbulyan.blog.repositories.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private SecurityService securityService;
    @Mock
    private ArticleRepository articleRepository;
    @InjectMocks
    private ArticleService underTest;

    @Test
    void publishArticle() {
        Long expectedId = 100000L;
        String expectedTitle = "Test title";
        String expectedText = "Test text";
        User expectedPublisher = new User();
        String publisherName = "testpublisher";
        expectedPublisher.setName(publisherName);
        when(articleRepository.save(any(Article.class))).then((Answer<Article>) invocation -> {
            Article argument = invocation.getArgument(0, Article.class);
            argument.setArticleId(expectedId);
            return argument;
        });
        when(userService.getReferenceByName(publisherName)).thenReturn(expectedPublisher);
        ArticlePublishedResponse articlePublishedResponse = assertDoesNotThrow(()-> underTest.publishArticle(new CreateArticleRequest(expectedTitle, expectedText), publisherName));
        assertEquals(expectedId, articlePublishedResponse.articleId());
        ArgumentCaptor<Article> articleArgumentCaptor = ArgumentCaptor.forClass(Article.class);
        verify(articleRepository, only()).save(articleArgumentCaptor.capture());
        Article actualArticle = articleArgumentCaptor.getValue();
        assertEquals(expectedTitle, actualArticle.getTitle());
        assertEquals(expectedText, actualArticle.getText());
        assertEquals(expectedPublisher, actualArticle.getPublisher());
    }

    @Test
    @DisplayName("get by id should return if article exists")
    void getByIdShouldReturnIfArticleExists() {
        Long articleId = 1L;
        ArticleResponse expectedResponse = new ArticleResponse("Test article", "Test text", "testpublisher");
        when(articleRepository.findByArticleId(articleId, ArticleResponse.class)).thenReturn(Optional.of(expectedResponse));
        ArticleResponse actualResponse = assertDoesNotThrow(() -> underTest.getById(articleId));
        assertSame(expectedResponse, actualResponse);
        verify(articleRepository, only()).findByArticleId(articleId, ArticleResponse.class);
    }
    
    @Test
    @DisplayName("get by id should throw ArticleNotFoundException if article doesn't exist")
    void getByIdShouldThrowIfArticleDoesNotExist(){
        Long articleId = 1L;
        when(articleRepository.findByArticleId(articleId, ArticleResponse.class)).thenReturn(Optional.empty());
        assertThrows(ArticleNotFoundException.class, ()->underTest.getById(articleId));
    }

    @Test
    void normalDeleteById() {
        String authorName = "testauthor";
        Long articleId = 1L;
        Authentication authentication = mock(Authentication.class);
        when(articleRepository.findArticleAuthorNameByArticleId(articleId)).thenReturn(Optional.of(authorName));
        doAnswer(invocation -> {
            invocation.getArgument(2, Runnable.class).run();
            return null;
        }).when(securityService).executeIfExecutorIsAdminOrEqualToTarget(same(authentication), same(authorName), any());
        assertDoesNotThrow(()->underTest.deleteById(articleId, authentication));
        verify(articleRepository, times(1)).deleteById(articleId);
    }

    @Test
    @DisplayName("deleteById should throw ArticleNotFoundException if author name was empty")
    void deleteByIdWhenAuthorNameWasNotFound() {
        long articleId = 1L;
        when(articleRepository.findArticleAuthorNameByArticleId(articleId)).thenReturn(Optional.empty());
        assertThrows(ArticleNotFoundException.class, ()->underTest.deleteById(articleId, mock(Authentication.class)));
        verify(articleRepository, never()).deleteById(any());
        verify(articleRepository, times(1)).findArticleAuthorNameByArticleId(articleId);
        verifyNoMoreInteractions(articleRepository);
    }
    @Test
    @DisplayName("getReferenceById when article exists by id")
    void getReferenceByIdForExistingArticle(){
        long articleId = 1L;
        Article expectedArticle = new Article();
        when(articleRepository.existsById(articleId)).thenReturn(true);
        when(articleRepository.getReferenceById(articleId)).thenReturn(expectedArticle);
        Article actualArticle = assertDoesNotThrow(() -> underTest.getReferenceById(articleId));
        assertSame(expectedArticle, actualArticle);
    }
    @Test
    @DisplayName("getReferenceById when article doesn't exists by id")
    void getReferenceByIdForNotExistingArticle(){
        long articleId = 1L;
        when(articleRepository.existsById(articleId)).thenReturn(false);
        assertThrows(ArticleNotFoundException.class, ()-> underTest.getReferenceById(articleId));
        verify(articleRepository, only()).existsById(articleId);
    }
    @Test
    @DisplayName("existById if article doesn't exist")
    void existsByIdIfArticleDoesNotExist(){
        long id = 1L;
        when(articleRepository.existsById(id)).thenReturn(true);
        assertTrue(underTest.existsById(id));
    }
    @Test
    @DisplayName("existById if article exists")
    void existsByIdIfArticleExists(){
        long id = 1L;
        when(articleRepository.existsById(id)).thenReturn(false);
        assertFalse(underTest.existsById(id));
    }
    @Test
    @DisplayName("updateArticle if executor name equal to author name")
    void updateArticleIfExecutorEqualAuthor(){
        long articleId = 1L;
        String authorName = "testauthor";
        String expectedText = "test text";
        String expectedTitle = "test title";
        when(articleRepository.findArticleAuthorNameByArticleId(articleId)).thenReturn(Optional.of(authorName));
        assertDoesNotThrow(()->underTest.updateArticle(articleId, expectedTitle, expectedText, authorName));
        verify(articleRepository, times(1)).updateTitleAndTextByArticleId(articleId, expectedTitle, expectedText);
        verify(articleRepository, times(1)).findArticleAuthorNameByArticleId(articleId);
        verifyNoMoreInteractions(articleRepository);
    }

    @Test
    void updateArticleIfAuthorNotFound() {
        long id = 1L;
        when(articleRepository.findArticleAuthorNameByArticleId(id)).thenReturn(Optional.empty());
        assertThrows(ArticleNotFoundException.class, ()->underTest.updateArticle(id, "Test title", "Test text", "test"));
        verify(articleRepository, never()).updateTitleAndTextByArticleId(any(), any(), any());
    }

    @Test
    void updateArticleIfAuthorNotEqualExecutor() {
        long articleId = 1L;
        String authorName = "testauthor";
        when(articleRepository.findArticleAuthorNameByArticleId(articleId)).thenReturn(Optional.of(authorName));
        assertThrows(AccessDeniedException.class, ()->underTest.updateArticle(articleId, "Test text", "ttlte", "executor"));
        verify(articleRepository, never()).updateTitleAndTextByArticleId(any(), any(), any());
    }
    @Test
    void getInfoAboutAll() {
        int pageNumber = 0;
        int pageSize = 10;
        Specification<Article> mockSpec = mock(Specification.class);
        Page<ArticleInfoDTO> expectedPage = mock(Page.class);
        when(articleRepository.findBy(same(mockSpec), any(Function.class))).thenReturn(expectedPage);
        Page<ArticleInfoDTO> actualPage = assertDoesNotThrow(() -> underTest.getInfoAboutAll(mockSpec, pageSize, pageNumber));
        assertSame(expectedPage, actualPage);
        ArgumentCaptor<Function<FluentQuery.FetchableFluentQuery<Article>, Page<ArticleInfoDTO>>> uArgumentCaptor = ArgumentCaptor.forClass(Function.class);
        verify(articleRepository, only()).findBy(same(mockSpec), uArgumentCaptor.capture());
        var actualFunction = uArgumentCaptor.getValue();
        FluentQuery.FetchableFluentQuery fluentQueryMock = mock(FluentQuery.FetchableFluentQuery.class);
        when(fluentQueryMock.project("articleId", "title", "publishDate", "publisher.name")).thenReturn(fluentQueryMock);
        when(fluentQueryMock.as(ArticleInfoDTO.class)).thenReturn(fluentQueryMock);
        when(fluentQueryMock.page(PageRequest.of(pageNumber, pageSize))).thenReturn(expectedPage);
        Page page = actualFunction.apply(fluentQueryMock);
        assertSame(expectedPage, page);
    }
}