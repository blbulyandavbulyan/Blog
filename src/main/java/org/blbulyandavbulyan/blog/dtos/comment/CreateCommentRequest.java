package org.blbulyandavbulyan.blog.dtos.comment;

import org.blbulyandavbulyan.blog.annotations.validation.article.ValidArticleId;
import org.blbulyandavbulyan.blog.annotations.validation.comment.ValidCommentText;

/**
 * DTO для комментария, отправляемое пользователем в виде JSON для публикации
 * @param articleId ИД статьи, под которой будет опубликован комментарий
 * @param text текст комментария
 */
public record CreateCommentRequest(@ValidArticleId Long articleId, @ValidCommentText String text) {
}
