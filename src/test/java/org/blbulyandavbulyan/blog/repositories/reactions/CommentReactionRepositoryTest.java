package org.blbulyandavbulyan.blog.repositories.reactions;

import org.blbulyandavbulyan.blog.dtos.reactions.ReactionStatistics;
import org.blbulyandavbulyan.blog.entities.Article;
import org.blbulyandavbulyan.blog.entities.Comment;
import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.entities.reactions.CommentReaction;
import org.blbulyandavbulyan.blog.entities.reactions.ReactionId;
import org.blbulyandavbulyan.blog.repositories.ArticleRepository;
import org.blbulyandavbulyan.blog.repositories.CommentRepository;
import org.blbulyandavbulyan.blog.repositories.RepositoryTestUtils;
import org.blbulyandavbulyan.blog.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentReactionRepositoryTest {
    @Autowired
    private IReactionRepository<CommentReaction> underTest;
    @Autowired
    private CommentReactionRepository commentReactionRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    private void assertEqualReactions(CommentReaction actual, CommentReaction expected){
        assertThat(actual.getTarget()).isEqualTo(expected.getTarget());
        assertThat(actual.getLiker()).isEqualTo(expected.getLiker());
        assertThat(actual.isLiked()).isEqualTo(expected.isLiked());
    }

    private Article createAndSaveArticle(User author){
        return articleRepository.save(new Article(author, "Test title", "test text"));
    }
    private CommentReaction createAndSaveReaction(User liker, Comment target){
        return underTest.save(new CommentReaction(target, liker));
    }
    private CommentReaction createAndSaveReaction(User liker, Comment target, boolean liked){
        CommentReaction reaction = new CommentReaction(target, liker);
        reaction.setLiked(liked);
        return commentReactionRepository.saveAndFlush(reaction);
    }
    private Comment createAndSaveComment(User author){
        return commentRepository.save(new Comment(author, createAndSaveArticle(author), "Test text"));
    }
    @AfterEach
    void clearDB(){
        commentReactionRepository.deleteAll();
        commentRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();
    }
    @Test
    void findByTargetIdAndLikerNameIfExists() {
        User liker = RepositoryTestUtils.createAndSaveUser(userRepository, "testuser");
        User commentAuthor = RepositoryTestUtils.createAndSaveUser(userRepository, "commenter");
        Comment target = createAndSaveComment(commentAuthor);
        CommentReaction expected = createAndSaveReaction(liker, target);
        Optional<CommentReaction> optionalCommentReaction = underTest.findByTargetIdAndLikerName(target.getId(), liker.getName());
        assertThat(optionalCommentReaction).isPresent();
        assertEqualReactions(optionalCommentReaction.get(), expected);
    }

    @Test
    void findByTargetIdAndLikerNameIfNotExists() {
        assertThat(underTest.findByTargetIdAndLikerName(1L, "testuser")).isEmpty();
    }
    @Test
    void deleteByTargetIdAndLikerName(){
        //для комплексного теста, нужно сделать так, чтобы на удаляемую цель отреагировало два человека
        //И так же, чтобы была другая реакция, на которую тоже кто-то другой отреагировал, или тот же
        User commentAuthor = RepositoryTestUtils.createAndSaveUser(userRepository, "commenter");
        User otherUser = RepositoryTestUtils.createAndSaveUser(userRepository, "otheruser1");
        Comment comment1 = createAndSaveComment(commentAuthor);
        Comment comment2 = createAndSaveComment(otherUser);
        CommentReaction deletingReaction = createAndSaveReaction(commentAuthor, comment1);
        CommentReaction notDeletingReaction1 = createAndSaveReaction(otherUser, comment1);
        CommentReaction notDeletingReaction2 = createAndSaveReaction(otherUser, comment2);
        underTest.deleteByTargetIdAndLikerName(deletingReaction.getTarget().getId(), deletingReaction.getLiker().getName());
        assertThat(commentReactionRepository.existsById(new ReactionId(
                deletingReaction.getTarget().getId(),
                deletingReaction.getLiker().getUserId()
        ))).isFalse();
        assertThat(commentReactionRepository.existsById(new ReactionId(
                notDeletingReaction1.getTarget().getId(),
                notDeletingReaction1.getLiker().getUserId()
        ))).isTrue();
        assertThat(commentReactionRepository.existsById(new ReactionId(
                notDeletingReaction2.getTarget().getId(),
                notDeletingReaction2.getLiker().getUserId()
        ))).isTrue();
    }

    @Test
    void getStatisticsWhenTargetNotExists(){
        ReactionStatistics statistics = underTest.getStatistics(1L);
        assertThat(statistics.dislikesCount()).isEqualTo(0L);
        assertThat(statistics.likesCount()).isEqualTo(0L);
    }
    @Test
    void getStatisticsWhenTargetExistsAndNoReactions() {
        Comment target = createAndSaveComment(RepositoryTestUtils.createAndSaveUser(userRepository, "commentauthor"));
        ReactionStatistics statistics = underTest.getStatistics(target.getId());
        assertThat(statistics.dislikesCount()).isEqualTo(0L);
        assertThat(statistics.likesCount()).isEqualTo(0L);
    }
    @Test
    void getStatisticsWhenTargetExistsAndReactionsFound(){
        Comment target = createAndSaveComment(RepositoryTestUtils.createAndSaveUser(userRepository, "commentauthor"));
        createAndSaveReaction(RepositoryTestUtils.createAndSaveUser(userRepository, "test1"), target, true);
        createAndSaveReaction(RepositoryTestUtils.createAndSaveUser(userRepository, "test2"), target, false);
        createAndSaveReaction(RepositoryTestUtils.createAndSaveUser(userRepository, "test3"), target, false);
        createAndSaveReaction(RepositoryTestUtils.createAndSaveUser(userRepository, "test4"), target, true);
        ReactionStatistics statistics = underTest.getStatistics(target.getId());
        assertThat(statistics.likesCount()).isEqualTo(2);
        assertThat(statistics.dislikesCount()).isEqualTo(2);
    }
}