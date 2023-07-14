package org.blbulyandavbulyan.blog.repositories;

import org.blbulyandavbulyan.blog.entities.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
