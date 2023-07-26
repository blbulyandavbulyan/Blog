package org.blbulyandavbulyan.blog.services;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.comment.CommentDto;
import org.blbulyandavbulyan.blog.entities.Comment;
import org.blbulyandavbulyan.blog.exceptions.articles.ArticleNotFoundException;
import org.blbulyandavbulyan.blog.repositories.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с комментариями
 */
@Service
@RequiredArgsConstructor
public class CommentService {
    /**
     * Репозиторий с комментариями
     */
    private final CommentRepository commentRepository;
    /**
     * Сервис для работы с пользователями
     */
    private final UserService userService;
    /**
     * Сервис для работы со статьями
     */
    private final ArticlesService articlesService;

    /**
     * Получает комментарии для определённой статьи
     * @param articleId ИД статьи
     * @param pageNumber номер страницы(начиная с нуля)
     * @param pageSize размер страницы
     * @return страница, содержащая искомые комментарии
     */
    public Page<CommentDto> getCommentDTOsForArticleId(Long articleId, int pageNumber, int pageSize){
        if(!articlesService.existsById(articleId))
            throw new ArticleNotFoundException("Article with articleId " + articleId + " not found!");
        return commentRepository.findAllByArticleArticleId(articleId, PageRequest.of(pageNumber, pageSize), CommentDto.class);
    }

    /**
     * Публикует комментарий к заданной статьи от заданного автора
     * @param publisherName имя публикующего пользователя
     * @param articleId ИД статьи, для которой нужно опубликовать комментарий
     * @param text текст комментария
     */
    public void publishComment(String publisherName, Long articleId, String text){
        // FIXME: 26.07.2023 Баг с 500-ой ошибкой, из-за того что не проверяется существование статьи
        // FIXME: 26.07.2023 Так же возможен аналогичный баг, если пользователя не существует
        Comment comment = new Comment();
        comment.setText(text);
        comment.setAuthor(userService.getReferenceByName(publisherName));
        comment.setArticle(articlesService.getReferenceById(articleId));
        commentRepository.save(comment);
    }
}
