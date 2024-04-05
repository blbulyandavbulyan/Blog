package org.blbulyandavbulyan.blog.dtos.comment;

import org.blbulyandavbulyan.blog.annotations.validation.comment.ValidCommentText;

/**
 * DTO для комментария, отправляемое пользователем в виде JSON для публикации
 * @param text текст комментария
 */
public record CreateCommentRequest(@ValidCommentText String text) {
}
