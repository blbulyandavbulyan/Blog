package org.blbulyandavbulyan.blog.repositories;

import org.blbulyandavbulyan.blog.entities.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
}
