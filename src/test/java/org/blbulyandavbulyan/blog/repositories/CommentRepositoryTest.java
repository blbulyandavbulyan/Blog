package org.blbulyandavbulyan.blog.repositories;

import jakarta.persistence.EntityManager;
import org.blbulyandavbulyan.blog.entities.Article;
import org.blbulyandavbulyan.blog.entities.Comment;
import org.blbulyandavbulyan.blog.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private CommentRepository underTest;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private EntityManager entityManager;
    @AfterEach
    public void clearDb(){
        underTest.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();
    }
    private User createAndSaveUser() {
        User publisher = new User();
        publisher.setName("davdfdsfafid");
        publisher.setPasswordHash("fdfdf");
        return userRepository.saveAndFlush(publisher);
    }
    private Article createAndSaveArticle(){
        return articleRepository.save(new Article(createAndSaveUser(), "Test title", "Test text"));
    }
    @Test
    void deleteById() {
        Article article = createAndSaveArticle();
        Long commentId = underTest.saveAndFlush(new Comment(article.getPublisher(), article, "Test text")).getId();
        Long commentId2 = underTest.saveAndFlush(new Comment(article.getPublisher(), article, "Test text2")).getId();
        underTest.deleteById(commentId);
        assertThat(underTest.existsById(commentId)).isFalse();
        assertThat(underTest.existsById(commentId2)).isTrue();
    }

    @Test
    void updateTextByCommentId() {
        Article article = createAndSaveArticle();
        String expectedOldText = "Test text2";
        String expectedNewText = "New text for comment";
        Long commentId = underTest.saveAndFlush(new Comment(article.getPublisher(), article, "Test text")).getId();
        Long commentId2 = underTest.saveAndFlush(new Comment(article.getPublisher(), article, expectedOldText)).getId();
        underTest.updateTextByCommentId(commentId, expectedNewText);
        entityManager.clear();
        Optional<Comment> commentOptional = underTest.findById(commentId);
        assertThat(commentOptional).isPresent();
        assertThat(commentOptional.get().getText()).isEqualTo(expectedNewText);
        Optional<Comment> comment2Optional = underTest.findById(commentId2);
        assertThat(comment2Optional).isPresent();
        assertThat(comment2Optional.get().getText()).isEqualTo(expectedOldText);
    }

    @Test
    void findCommentAuthorNameByCommentIdWhenCommentExist() {
        Article article = createAndSaveArticle();
        Long commentId = underTest.saveAndFlush(new Comment(article.getPublisher(), article, "Test text")).getId();
        Optional<String> authorName = underTest.findCommentAuthorNameByCommentId(commentId);
        assertThat(authorName).isPresent();
        assertThat(authorName.get()).isEqualTo(article.getPublisher().getName());
    }

    @Test
    void findCommentAuthorNameWhenCommentDoesNotExist() {
        assertThat(underTest.findCommentAuthorNameByCommentId(200L)).isEmpty();
    }
}