package org.blbulyandavbulyan.blog.dtos.reactions;

import org.blbulyandavbulyan.blog.annotations.validation.comment.ValidCommentId;

/**
 * Данный класс является DTO для создания/изменения реакции на комментарий
 * @param commentId ИД комментария для реакции
 * @param liked понравился ли пользователю комментарий
 */
public record CommentReactionDTO (@ValidCommentId Long commentId, boolean liked){
}
