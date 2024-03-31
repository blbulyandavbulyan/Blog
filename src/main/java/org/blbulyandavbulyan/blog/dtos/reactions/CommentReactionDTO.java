package org.blbulyandavbulyan.blog.dtos.reactions;

/**
 * Данный класс является DTO для создания/изменения реакции на комментарий
 * @param liked понравился ли пользователю комментарий
 */
public record CommentReactionDTO (boolean liked){
}
