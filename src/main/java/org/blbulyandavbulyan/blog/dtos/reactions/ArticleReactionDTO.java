package org.blbulyandavbulyan.blog.dtos.reactions;

import org.blbulyandavbulyan.blog.annotations.validation.article.ValidArticleId;

/**
 * Данный класс предоставляет DTO для создания/изменения реакций на статью
 * @param articleId ИД статьи для реакции
 * @param liked понравилась ли статья
 */
public record ArticleReactionDTO (@ValidArticleId Long articleId, boolean liked){
}
