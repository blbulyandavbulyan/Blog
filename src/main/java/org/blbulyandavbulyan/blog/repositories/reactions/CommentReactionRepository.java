package org.blbulyandavbulyan.blog.repositories.reactions;

import org.blbulyandavbulyan.blog.entities.reactions.CommentReaction;
import org.blbulyandavbulyan.blog.entities.reactions.ReactionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, ReactionId>, IReactionRepository<CommentReaction>{
}