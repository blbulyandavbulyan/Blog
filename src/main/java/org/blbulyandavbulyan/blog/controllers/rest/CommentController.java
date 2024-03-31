package org.blbulyandavbulyan.blog.controllers.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.annotations.validation.article.ValidArticleId;
import org.blbulyandavbulyan.blog.annotations.validation.comment.ValidCommentId;
import org.blbulyandavbulyan.blog.annotations.validation.page.ValidPageNumber;
import org.blbulyandavbulyan.blog.annotations.validation.page.ValidPageSize;
import org.blbulyandavbulyan.blog.dtos.comment.CommentResponse;
import org.blbulyandavbulyan.blog.dtos.comment.CreateCommentRequest;
import org.blbulyandavbulyan.blog.dtos.comment.EditCommentRequest;
import org.blbulyandavbulyan.blog.services.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Контроллер для работы с комментариями
 */
@RestController
@RequestMapping("/api/v1/comments")//TODO 31.03.2024: возможно придётся убрать Request mapping, т.к. не все пути будут начинаться здесь с /api/v1/comments
@RequiredArgsConstructor
@Validated
@Tag(name = "comment")
public class CommentController {
    /**
     * Сервис для работы с комментариями
     */
    private final CommentService commentService;

    /**
     * Получает все комментарии к заданной статье в виде страницы
     * @param articleId ИД страницы
     * @param pageNumber номер страницы(начиная с 1)
     * @param pageSize размер страницы
     * @return страницу, содержащую комментарии к заданной статье
     */
    @GetMapping("/article/{articleId}")
    public Page<CommentResponse> getAllCommentsForArticle(@ValidArticleId @PathVariable Long articleId,
                                                          @ValidPageNumber @RequestParam(name = "p", defaultValue = "1") Integer pageNumber,
                                                          @ValidPageSize @RequestParam(name = "s", defaultValue = "10") Integer pageSize){
        //TODO 31.03.2024: сменить path для этого ednpoint на /api/v1/articles/{articleId}/comments, либо переместить этот метод в контроллер ArticleController
        return commentService.getCommentDTOsForArticleId(articleId, pageNumber - 1, pageSize);
    }

    /**
     * Публикует полученный комментарий
     * @param commentForPublishing DTO комментария, который нужно опубликовать
     * @param principal ссылка на principal, в котором будет имя авторизованного пользователя
     */
    @Secured({"ROLE_COMMENTER"})
    @PostMapping("/article")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse publishComment(@Validated @RequestBody CreateCommentRequest commentForPublishing, Principal principal){
        //TODO 31.03.2024: периеменовать этот endpoint, убрать article с конца
        return commentService.publishComment(principal.getName(), commentForPublishing.articleId(), commentForPublishing.text());
    }
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable @ValidCommentId Long commentId, Authentication authentication){
        commentService.deleteComment(commentId, authentication);
    }
    @PatchMapping
    public void editComment(@Validated @RequestBody EditCommentRequest editCommentRequest, Principal principal){
        //TODO 31.03.2024: возможно стоит вынести commentId в path variable здесь
        commentService.editComment(editCommentRequest.commentId(), editCommentRequest.text(), principal.getName());
    }
}
