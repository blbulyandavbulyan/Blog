package org.blbulyandavbulyan.blog.repositories;

import org.blbulyandavbulyan.blog.entities.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
//    @Query("SELECT new org.blbulyandavbulyan.blog.dtos.article.ArticleDto(a.title, a.text, a.publisher.name) FROM #{#entityName} a WHERE a.id = :articleId")
    <T> Optional<T> findByArticleId(Long articleId, Class<T> dtoType);

    <T> Page<T> findAllPagesBy(Class<T> type, final Pageable pageable);

}
