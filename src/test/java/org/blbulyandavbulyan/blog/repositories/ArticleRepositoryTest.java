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

    private User createAndSaveUser() {
        User publisher = new User();
        publisher.setName("davdfdsfafid");
        publisher.setPasswordHash("fdfdf");
        return userRepository.saveAndFlush(publisher);
    }

    @Test
    void findArticleDtoById() {
        //give
        User publisher = createAndSaveUser();
        Article article = new Article(publisher, "Test title", "Test text");
        underTest.save(article);
        //when
        Optional<ArticleResponse> articleDtoOptional = underTest.findByArticleId(article.getArticleId(), ArticleResponse.class);
        //then
        assertThat(articleDtoOptional).isPresent();
        ArticleResponse articleResponse = articleDtoOptional.get();
        assertThat(articleResponse.publisherName()).isEqualTo(publisher.getName());
        assertThat(articleResponse.text()).isEqualTo(article.getText());
        assertThat(articleResponse.title()).isEqualTo(article.getTitle());
    }

    @Test
    void findArticleDtoByIdShouldReturnEmptyOptionalIfArticleDoesNotExists() {
        Optional<ArticleResponse> articleDtoOptional = underTest.findByArticleId(2L, ArticleResponse.class);
        assertThat(articleDtoOptional).isNotPresent();
    }

    @Test
    void findAllPagesBy() {
        User publisher = createAndSaveUser();
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
                new ArticleInfoDTOImpl(a.getArticleId(), a.getPublisher().getName(), a.getPublishDate(), a.getTitle())
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
        Article article = underTest.saveAndFlush(new Article(createAndSaveUser(), "test title", "test text"));
        long articleId = article.getArticleId();
        String expectedText = "New text";
        String expectedTitle = "New Title";
        underTest.updateTitleAndTextByArticleId(articleId, expectedTitle, expectedText);
        entityManager.clear();
        Optional<Article> articleOptional = underTest.findById(articleId);
        assertThat(articleOptional).isPresent();
        article = articleOptional.get();
        assertThat(article.getTitle()).isEqualTo(expectedTitle);
        assertThat(article.getText()).isEqualTo(expectedText);
    }
}