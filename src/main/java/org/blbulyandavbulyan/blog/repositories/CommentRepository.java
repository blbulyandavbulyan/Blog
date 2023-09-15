package org.blbulyandavbulyan.blog.repositories;

import org.blbulyandavbulyan.blog.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    <T> Page<T> findAllByArticleArticleId(Long id, Pageable pageable, Class<T> dtoType);
    Optional<String> find_AuthorNameByCommentId(Long commentId);
}
