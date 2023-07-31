package org.blbulyandavbulyan.blog.repositories;

import org.blbulyandavbulyan.blog.entities.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {

    <T> Optional<T> findByArticleId(Long articleId, Class<T> dtoType);

    <T> Page<T> findAllPagesBy(Class<T> type, final Pageable pageable);

}
