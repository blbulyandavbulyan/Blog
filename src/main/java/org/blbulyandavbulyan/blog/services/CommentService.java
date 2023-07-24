package org.blbulyandavbulyan.blog.services;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.comment.CommentDto;
import org.blbulyandavbulyan.blog.entities.Comment;
import org.blbulyandavbulyan.blog.exceptions.articles.ArticleNotFoundException;
import org.blbulyandavbulyan.blog.repositories.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final ArticlesService articlesService;
    public Page<CommentDto> getCommentDTOsForArticleId(Long id, int pageNumber, int pageSize){
        if(!articlesService.existsById(id))
            throw new ArticleNotFoundException("Article with id " + id + " not found!");
        return commentRepository.findAllByArticleArticleId(id, PageRequest.of(pageNumber, pageSize), CommentDto.class);
    }
    public void publishComment(String publisherName, Long articleId, String text){
        Comment comment = new Comment();
        comment.setText(text);
        comment.setAuthor(userService.getReferenceByName(publisherName));
        comment.setArticle(articlesService.getReferenceById(articleId));
        commentRepository.save(comment);
    }
}
