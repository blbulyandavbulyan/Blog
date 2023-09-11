package org.blbulyandavbulyan.blog.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.comment.CommentDTOForPublishing;
import org.blbulyandavbulyan.blog.dtos.comment.CommentDto;
import org.blbulyandavbulyan.blog.services.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Контроллер для работы с комментариями
 */
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
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
    public Page<CommentDto> getAllCommentsForArticle(@PathVariable Long articleId, @RequestParam(name = "p", defaultValue = "1") Integer pageNumber, @RequestParam(name = "s", defaultValue = "10") Integer pageSize){
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
    public void publishComment(@Validated @RequestBody CommentDTOForPublishing commentForPublishing, Principal principal){
        commentService.publishComment(principal.getName(), commentForPublishing.articleId(), commentForPublishing.text());
    }
}
