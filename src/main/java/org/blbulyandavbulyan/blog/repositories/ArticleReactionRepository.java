package org.blbulyandavbulyan.blog.repositories;

import org.blbulyandavbulyan.blog.entities.Article;
import org.blbulyandavbulyan.blog.entities.ArticleReaction;
import org.blbulyandavbulyan.blog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleReactionRepository extends JpaRepository<ArticleReaction, Long> {
    Optional<ArticleReaction> findByArticleAndLiker(Article article, User liker);
    void deleteByArticleAndLiker(Article article, User liker);
}