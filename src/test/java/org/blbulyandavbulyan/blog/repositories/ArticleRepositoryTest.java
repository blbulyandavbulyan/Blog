package org.blbulyandavbulyan.blog.repositories;

import org.blbulyandavbulyan.blog.dtos.article.ArticleDto;
import org.blbulyandavbulyan.blog.dtos.article.ArticleInfoDTO;
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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class ArticleRepositoryTest {
    /**
     * DTO для представления базовой информации о статье, для отправки клиенту
     * @param articleId ИД статьи
     * @param publisherName имя автора статьи
     * @param publishDate дата публикации
     * @param title название статьи
     */
    private record ArticleInfoDTOImpl(Long articleId, String publisherName, ZonedDateTime publishDate, String title) implements ArticleInfoDTO {
        @Override
        public Long getArticleId() {
            return articleId;
        }

        @Override
        public UserDto getPublisher() {
            return () -> publisherName;
        }

        @Override
        public ZonedDateTime getPublishDate() {
            return publishDate;
        }

        @Override
        public String getTitle() {
            return title;
        }
    }
    @Autowired ArticleRepository underTest;
    @Autowired UserRepository userRepository;
    @BeforeEach
    public void clearDB(){
        underTest.deleteAll();
        userRepository.deleteAll();
    }
    private User createAndSaveUser(){
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
        Optional<ArticleDto> articleDtoOptional = underTest.findByArticleId(article.getArticleId(), ArticleDto.class);
        //then
        assertThat(articleDtoOptional).isPresent();
        ArticleDto articleDto = articleDtoOptional.get();
        assertThat(articleDto.publisherName()).isEqualTo(publisher.getName());
        assertThat(articleDto.text()).isEqualTo(article.getText());
        assertThat(articleDto.title()).isEqualTo(article.getTitle());
    }
    @Test
    void findArticleDtoByIdShouldReturnEmptyOptionalIfArticleDoesNotExists(){
        Optional<ArticleDto> articleDtoOptional = underTest.findByArticleId(2L, ArticleDto.class);
        assertThat(articleDtoOptional).isNotPresent();
    }
    @Test
    void findAllPagesBy() {
        User publisher = createAndSaveUser();
        Collection<Article> articles = List.of(new Article(publisher, "Article 1", "Text 1"), new Article(publisher, "Aritcle 2", "Text 2"), new Article(publisher, "Article 3", "Text 3"));
        articles.forEach(article -> article.setPublishDate(ZonedDateTime.now()));
        underTest.saveAllAndFlush(articles);
        Page<? extends ArticleInfoDTO> page = underTest.findAllPagesBy(ArticleInfoDTOImpl.class, PageRequest.of(0, 4));
        List<? extends ArticleInfoDTO> actual = page.get().toList();
        Set<ArticleInfoDTO> expected = articles.stream().map(a->new ArticleInfoDTOImpl(a.getArticleId(), a.getPublisher().getName(), a.getPublishDate(), a.getTitle())).collect(Collectors.toSet());
        Assertions.assertTrue(expected.containsAll(actual));
    }
    @Test
    void findAllPagesByShouldReturnEmptyPageIfThereIsNoArticles(){
        Page<? extends ArticleInfoDTO> page = underTest.findAllPagesBy(ArticleInfoDTOImpl.class, PageRequest.of(0, 4));
        assertThat(page.isEmpty()).isTrue();
    }
}