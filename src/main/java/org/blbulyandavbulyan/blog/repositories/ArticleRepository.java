package org.blbulyandavbulyan.blog.repositories;

import org.blbulyandavbulyan.blog.entities.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {
    @Transactional
    @Modifying
    @Query("update Article a set a.title = :title, a.text = :text where a.id = :articleId")
    void updateTitleAndTextByArticleId(@Param("articleId") Long articleId, @Param("title") String title, @Param("text") String text);

    <T> Optional<T> findById(Long articleId, Class<T> dtoType);

    <T> Page<T> findAllPagesBy(Class<T> type, final Pageable pageable);
    @Query("SELECT a.publisher.name FROM Article a WHERE a.id = :id")
    Optional<String> findArticleAuthorNameByArticleId(@Param("id") Long id);

    @Override
    @Modifying
    @Query("DELETE FROM Article a WHERE a.id = :id")
    void deleteById(@NonNull @Param("id") Long articleId);
}
