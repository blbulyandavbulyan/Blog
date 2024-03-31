package org.blbulyandavbulyan.blog.dtos.reactions;

/**
 * Данный класс предоставляет DTO для создания/изменения реакций на статью
 * @param liked понравилась ли статья
 */
public record ArticleReactionDTO (boolean liked){
}
