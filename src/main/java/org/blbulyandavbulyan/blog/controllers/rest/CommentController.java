package org.blbulyandavbulyan.blog.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.comment.CommentDTOForPublishing;
import org.blbulyandavbulyan.blog.dtos.comment.CommentDto;
import org.blbulyandavbulyan.blog.services.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    @GetMapping("/article/{articleId}")
    public Page<CommentDto> getAllCommentsForArticle(@PathVariable Long articleId, @RequestParam(name = "p", defaultValue = "1") Integer pageNumber, @RequestParam(name = "s", defaultValue = "10") Integer pageSize){
        return commentService.getCommentDTOsForArticleId(articleId, pageNumber - 1, pageSize);
    }
    @Secured({"ROLE_COMMENTER"})
    @PostMapping("/article")
    @ResponseStatus(HttpStatus.CREATED)
    public void publishComment(@RequestBody CommentDTOForPublishing commentForPublishing, Principal principal ){
        commentService.publishComment(principal.getName(), commentForPublishing.articleId(), commentForPublishing.text());
    }
}
