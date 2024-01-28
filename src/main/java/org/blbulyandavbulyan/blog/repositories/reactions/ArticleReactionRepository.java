package org.blbulyandavbulyan.blog.repositories.reactions;

import org.blbulyandavbulyan.blog.entities.reactions.ArticleReaction;
import org.blbulyandavbulyan.blog.entities.reactions.ReactionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleReactionRepository extends JpaRepository<ArticleReaction, ReactionId>, IReactionRepository<ArticleReaction>{
}