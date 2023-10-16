package org.blbulyandavbulyan.blog.repositories.reactions;

import org.blbulyandavbulyan.blog.entities.Article;
import org.blbulyandavbulyan.blog.entities.reactions.ArticleReaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleReactionRepository extends JpaRepository<ArticleReaction, Long>, IReactionRepository<ArticleReaction, Article>{
}