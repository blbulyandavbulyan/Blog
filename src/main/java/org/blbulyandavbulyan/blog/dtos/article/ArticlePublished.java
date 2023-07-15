package org.blbulyandavbulyan.blog.dtos.article;

/**
 * Данная запись отправляется пользователю в качестве ответа, в случае успешной публикации статьи
 * @param articleId ИД добавленной статьи
 */
public record ArticlePublished(Long articleId) {
}
