package org.blbulyandavbulyan.blog.repositories.reactions;

import org.blbulyandavbulyan.blog.dtos.reactions.ReactionStatistics;
import org.blbulyandavbulyan.blog.entities.Article;
import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.entities.reactions.ArticleReaction;
import org.blbulyandavbulyan.blog.repositories.ArticleRepository;
import org.blbulyandavbulyan.blog.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.blbulyandavbulyan.blog.repositories.RepositoryTestUtils.createAndSaveUser;

@DataJpaTest
class ArticleReactionRepositoryTest {
    @Autowired
    private IReactionRepository<ArticleReaction> underTest;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ArticleReactionRepository articleReactionRepository;
    private Article createAndSaveArticle(User author){
        return articleRepository.save(new Article(author, "Test title", "test text"));
    }

    private ArticleReaction createAndSaveReaction(Article target, User liker) {
        return createAndSaveReaction(target, liker, false);
    }

    private ArticleReaction createAndSaveReaction(Article target, User liker, boolean liked){
        ArticleReaction articleReaction = new ArticleReaction(target, liker);
        articleReaction.setLiked(liked);
        return underTest.save(articleReaction);
    }
    
    @Test
    void findByTargetIdAndLikerNameWhenReactionMatchesBothOfTheParameters() {
        Article target = createAndSaveArticle(createAndSaveUser(userRepository, "testuser1"));
        User liker = createAndSaveUser(userRepository, "test1");
        ArticleReaction expectedReaction = createAndSaveReaction(target, liker);
        createAndSaveReaction(target, createAndSaveUser(userRepository, "testuser2"));
        createAndSaveReaction(createAndSaveArticle(createAndSaveUser(userRepository, "testuser3")), liker);
        Optional<ArticleReaction> actualReactionOptional = underTest.findByTargetIdAndLikerName(target.getId(), liker.getName());
        assertThat(actualReactionOptional).isPresent();
        ArticleReaction actual = actualReactionOptional.get();
        assertThat(actual.getId()).isEqualTo(expectedReaction.getId());
        assertThat(actual.getLiker()).isEqualTo(expectedReaction.getLiker());
        assertThat(actual.getTarget()).isEqualTo(expectedReaction.getTarget());
    }

    @Test
    void findByTargetIdAndLikerNameWhenReactionDoesNotExist() {
        Optional<ArticleReaction> testuser1 = underTest.findByTargetIdAndLikerName(1L, "testuser1");
        assertThat(testuser1).isEmpty();
    }

    @Test
    void deleteByTargetIdAndLikerName() {
        Article target = createAndSaveArticle(createAndSaveUser(userRepository, "testuser1"));
        User liker = createAndSaveUser(userRepository, "test1");
        ArticleReaction expectedDeletedReaction = createAndSaveReaction(target, liker);
        ArticleReaction expectedNotDeletedReaction1 = createAndSaveReaction(target, createAndSaveUser(userRepository, "testuser2"));
        ArticleReaction expectedNotDeletedReaction2 = createAndSaveReaction(createAndSaveArticle(createAndSaveUser(userRepository, "testuser3")), liker);
        underTest.deleteByTargetIdAndLikerName(target.getId(), liker.getName());
        assertThat(articleReactionRepository.existsById(expectedDeletedReaction.getId())).isFalse();
        assertThat(articleReactionRepository.existsById(expectedNotDeletedReaction1.getId())).isTrue();
        assertThat(articleReactionRepository.existsById(expectedNotDeletedReaction2.getId())).isTrue();
    }

    @Test
    void getStatisticsWhenTargetExistsAndNoReactions() {
        Article target = createAndSaveArticle(createAndSaveUser(userRepository, "testuser1"));
        ReactionStatistics statistics = underTest.getStatistics(target.getId());
        assertThat(statistics.likesCount()).isZero();
        assertThat(statistics.likesCount()).isZero();
    }

    @Test
    void getStatisticsWhenTargetDoesNotExists() {
        ReactionStatistics statistics = underTest.getStatistics(1L);
        assertThat(statistics.likesCount()).isZero();
        assertThat(statistics.likesCount()).isZero();
    }

    @Test
    void getStatisticsWhenTargetHasReactions() {
        Article target = createAndSaveArticle(createAndSaveUser(userRepository, "testuser1"));
        createAndSaveReaction(target, createAndSaveUser(userRepository, "testuser2"), false);
        createAndSaveReaction(target, createAndSaveUser(userRepository, "testuser3"), true);
        createAndSaveReaction(target, createAndSaveUser(userRepository, "testuser4"), false);
        ReactionStatistics statistics = underTest.getStatistics(target.getId());
        assertThat(statistics.likesCount()).isOne();
        assertThat(statistics.dislikesCount()).isEqualTo(2L);
    }
}