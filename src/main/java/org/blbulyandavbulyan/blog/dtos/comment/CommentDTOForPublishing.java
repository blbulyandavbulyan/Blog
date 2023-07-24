package org.blbulyandavbulyan.blog.dtos.comment;

/**
 * DTO для комментария, отправляемое пользователем в виде JSON для публикации
 * @param articleId ИД статьи, под которой будет опубликован комментарий
 * @param text текст комментария
 */
public record CommentDTOForPublishing(Long articleId, String text) {
}
