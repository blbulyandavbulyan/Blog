package org.blbulyandavbulyan.blog.repositories;

import jakarta.persistence.EntityManager;
import org.blbulyandavbulyan.blog.dtos.article.ArticleInfoDTO;
import org.blbulyandavbulyan.blog.dtos.article.ArticleResponse;
import org.blbulyandavbulyan.blog.entities.Article;
import org.blbulyandavbulyan.blog.entities.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.blbulyandavbulyan.blog.repositories.RepositoryTestUtils.createAndSaveUser;

@DataJpaTest
class ArticleRepositoryTest {

    @Autowired
    ArticleRepository underTest;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EntityManager entityManager;
    @BeforeEach
    public void clearDB() {
        underTest.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findArticleDtoById() {
        //give
        User publisher = createAndSaveUser(userRepository);
        Article article = new Article(publisher, "Test title", "Test text");
        underTest.save(article);
        //when
        Optional<ArticleResponse> articleDtoOptional = underTest.findById(article.getId(), ArticleResponse.class);
        //then
        assertThat(articleDtoOptional).isPresent();
        ArticleResponse articleResponse = articleDtoOptional.get();
        assertThat(articleResponse.publisherName()).isEqualTo(publisher.getName());
        assertThat(articleResponse.text()).isEqualTo(article.getText());
        assertThat(articleResponse.title()).isEqualTo(article.getTitle());
    }

    @Test
    void findArticleDtoByIdShouldReturnEmptyOptionalIfArticleDoesNotExists() {
        Optional<ArticleResponse> articleDtoOptional = underTest.findById(2L, ArticleResponse.class);
        assertThat(articleDtoOptional).isNotPresent();
    }

    @Test
    void findAllPagesBy() {
        User publisher = createAndSaveUser(userRepository);
        List<Article> articles = List.of(
                new Article(publisher, "Article 1", "Text 1"),
                new Article(publisher, "Aritcle 2", "Text 2"),
                new Article(publisher, "Article 3", "Text 3")
        );
        articles.forEach(article -> article.setPublishDate(ZonedDateTime.now()));
        articles = underTest.saveAllAndFlush(articles);
        Page<ArticleInfoDTOImpl> page = underTest.findAllPagesBy(ArticleInfoDTOImpl.class, PageRequest.of(0, 4));
        List<ArticleInfoDTOImpl> actual = page.get().toList();
        Set<ArticleInfoDTOImpl> expected = articles.stream().map(a ->
                new ArticleInfoDTOImpl(a.getId(), a.getPublisher().getName(), a.getPublishDate(), a.getTitle())
        ).collect(Collectors.toSet());
        Assertions.assertTrue(expected.containsAll(actual));
    }

    @Test
    void findAllPagesByShouldReturnEmptyPageIfThereIsNoArticles() {
        Page<? extends ArticleInfoDTO> page = underTest.findAllPagesBy(ArticleInfoDTOImpl.class, PageRequest.of(0, 4));
        assertThat(page.isEmpty()).isTrue();
    }

    @Test
    void updateArticleById() {
        Article article = underTest.saveAndFlush(new Article(createAndSaveUser(userRepository), "test title", "test text"));
        String expectedPublisherName = article.getPublisher().getName();
        ZonedDateTime expectedPublishDate = article.getPublishDate();
        long articleId = article.getId();
        String expectedText = "New text";
        String expectedTitle = "New Title";
        underTest.updateTitleAndTextByArticleId(articleId, expectedTitle, expectedText);
        entityManager.clear();
        Optional<Article> articleOptional = underTest.findById(articleId);
        assertThat(articleOptional).isPresent();
        article = articleOptional.get();
        assertThat(article.getTitle()).isEqualTo(expectedTitle);
        assertThat(article.getText()).isEqualTo(expectedText);
        assertThat(article.getPublisher().getName()).isEqualTo(expectedPublisherName);
        assertThat(article.getPublishDate()).isEqualTo(expectedPublishDate);
    }

    @Test
    void findAuthorNameByArticleIdWhenArticleExists() {
        User publisher = createAndSaveUser(userRepository);
        Article article = underTest.saveAndFlush(new Article(publisher, "test title", "test text"));
        Optional<String> authorNameOptional = underTest.findArticleAuthorNameByArticleId(article.getId());
        assertThat(authorNameOptional).isPresent();
        assertThat(authorNameOptional.get()).isEqualTo(publisher.getName());
    }

    @Test
    void findAuthorNameByArticleIdWhenArticleDoesNotExist() {
        Optional<String> authorNameOptional = underTest.findArticleAuthorNameByArticleId(200L);
        assertThat(authorNameOptional).isEmpty();
    }

    @Test
    void deleteArticleById() {
        //этот тест нужен поскольку в ArticleRepository для deleteById прописан Query
        Long articleId =  underTest.saveAndFlush(new Article(createAndSaveUser(userRepository), "test title", "test text")).getId();
        underTest.deleteById(articleId);
        boolean actualExistById = underTest.existsById(articleId);
        assertThat(actualExistById).isFalse();
    }
}