package org.blbulyandavbulyan.blog.dtos.article;

import java.time.ZonedDateTime;

/**
 * Данный класс предназначен для отображения пользователю статьи
 * @param title название статьи
 * @param text текст стать
 * @param publisherName имя автора данной статьи
 * @param id ИД статьи
 * @param publishDate  дата публикации статьи
 */
public record ArticleResponse(String title, String text, String publisherName, ZonedDateTime publishDate, Long id) {
}
