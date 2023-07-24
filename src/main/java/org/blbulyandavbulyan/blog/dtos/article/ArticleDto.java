package org.blbulyandavbulyan.blog.dtos.article;

/**
 * Данный класс предназначен для отображения пользователю статьи
 * @param title название статьи
 * @param text текст стать
 * @param publisherName имя автора данной статьи
 */
public record ArticleDto(String title, String text, String publisherName) {
}
