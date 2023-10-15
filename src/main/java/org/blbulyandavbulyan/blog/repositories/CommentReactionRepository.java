package org.blbulyandavbulyan.blog.repositories;

import org.blbulyandavbulyan.blog.entities.Comment;
import org.blbulyandavbulyan.blog.entities.CommentReaction;
import org.blbulyandavbulyan.blog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {
    void deleteByCommentAndLiker(Comment comment, User liker);
    Optional<CommentReaction> findByCommentAndLiker(Comment comment, User liker);
}