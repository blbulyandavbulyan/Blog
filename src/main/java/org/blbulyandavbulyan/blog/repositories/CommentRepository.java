package org.blbulyandavbulyan.blog.repositories;

import org.blbulyandavbulyan.blog.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Transactional
    @Modifying
    @Query("update Comment c set c.text = :text where c.commentId = :id")
    void updateTextByCommentId(@Param("id") Long commentId, @Param("text") String text);
    <T> Page<T> findAllByArticleArticleId(Long id, Pageable pageable, Class<T> dtoType);
    @Query("SELECT c.author.name FROM Comment c WHERE c.commentId = :commentId")
    Optional<String> findCommentAuthorNameByCommentId(@Param("commentId") Long commentId);

    @Override
    @Modifying
    @Query("DELETE FROM Comment WHERE commentId = :id")
    void deleteById(@NonNull @Param("id") Long commentId);
}
