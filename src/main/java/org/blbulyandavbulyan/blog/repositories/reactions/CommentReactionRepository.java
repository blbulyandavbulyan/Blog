package org.blbulyandavbulyan.blog.repositories.reactions;

import org.blbulyandavbulyan.blog.entities.reactions.CommentReaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long>, IReactionRepository<CommentReaction>{
}