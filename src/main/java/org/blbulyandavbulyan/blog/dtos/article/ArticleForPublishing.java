package org.blbulyandavbulyan.blog.dtos.article;

/**
 * Данная предназначена для приёма от пользователя статьи для публикации
 * @param title название статьи
 * @param text текст статьи
 */
public record ArticleForPublishing(String title, String text) {
}
