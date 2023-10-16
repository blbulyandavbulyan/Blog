package org.blbulyandavbulyan.blog.repositories.reactions;

import org.blbulyandavbulyan.blog.entities.Comment;
import org.blbulyandavbulyan.blog.entities.CommentReaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long>, IReactionRepository<CommentReaction, Comment>{
}