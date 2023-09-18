package org.blbulyandavbulyan.blog.services;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.comment.CommentResponse;
import org.blbulyandavbulyan.blog.entities.Comment;
import org.blbulyandavbulyan.blog.exceptions.AccessDeniedException;
import org.blbulyandavbulyan.blog.exceptions.articles.ArticleNotFoundException;
import org.blbulyandavbulyan.blog.exceptions.comments.CommentNotFoundException;
import org.blbulyandavbulyan.blog.repositories.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
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
    private final SecurityService securityService;
    /**
     * Получает комментарии для определённой статьи
     * @param articleId ИД статьи
     * @param pageNumber номер страницы(начиная с нуля)
     * @param pageSize размер страницы
     * @return страница, содержащая искомые комментарии
     */
    public Page<CommentResponse> getCommentDTOsForArticleId(Long articleId, int pageNumber, int pageSize){
        if(!articlesService.existsById(articleId))
            throw new ArticleNotFoundException("Article with articleId " + articleId + " not found!");
        return commentRepository.findAllByArticleArticleId(articleId, PageRequest.of(pageNumber, pageSize), CommentResponse.class);
    }

    /**
     * Публикует комментарий к заданной статьи от заданного автора
     * @param publisherName имя публикующего пользователя
     * @param articleId ИД статьи, для которой нужно опубликовать комментарий
     * @param text текст комментария
     * @throws ArticleNotFoundException если статья не найдена
     * @throws org.blbulyandavbulyan.blog.exceptions.users.UserNotFoundException если не найден публикатор
     */
    public CommentResponse publishComment(String publisherName, Long articleId, String text){
        Comment comment = new Comment();
        comment.setText(text);
        comment.setAuthor(userService.getReferenceByName(publisherName));
        comment.setArticle(articlesService.getReferenceById(articleId));
        comment = commentRepository.save(comment);
        return new CommentResponse(comment.getCommentId(), publisherName, comment.getText(), comment.getPublishDate());
    }

    /**
     * Удаляет комментарий с заданным id
     * @param commentId ID комментария, который нужно удалить
     * @param authentication объект authentication, необходимый методу для проверки, имени удаляющего и его ролей
     * @throws CommentNotFoundException если комментария с заданным ID нет
     * @throws org.blbulyandavbulyan.blog.exceptions.AccessDeniedException если у пользователя, который хочет удалить нет необходимых прав
     */
    public void deleteComment(Long commentId, Authentication authentication) {
        String authorName = commentRepository.findCommentAuthorNameByCommentId(commentId)
                .orElseThrow(()->new CommentNotFoundException("Comment with id " + commentId + " not found!"));
        securityService.executeIfExecutorIsAdminOrEqualToTarget(authentication, authorName, ()-> commentRepository.deleteById(commentId));
    }

    /**
     * Редактирует комментарий по id
     * @param commentId ID комментария, который нужно отредактировать
     * @param text новый текст комментария
     * @param executorName имя пользователя, который хочет отредактировать комментарий
     * @throws CommentNotFoundException если комментарий с заданным ID не найден
     * @throws AccessDeniedException если имя исполнителя не совпадает с именем автора комментария
     */
    public void editComment(Long commentId, String text, String executorName) {
        String authorName = commentRepository.findCommentAuthorNameByCommentId(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment with id " + commentId + " not found!"));
        if(authorName.equals(executorName))
            commentRepository.updateTextByCommentId(commentId, text);
        else throw new AccessDeniedException("Operation not permitted");
    }
}
