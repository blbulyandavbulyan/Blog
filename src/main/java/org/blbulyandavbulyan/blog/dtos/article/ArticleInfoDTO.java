package org.blbulyandavbulyan.blog.dtos.article;

import java.time.ZonedDateTime;

/**
 * DTO для представления базовой информации о статье, для отправки клиенту
 * @param articleId ИД статьи
 * @param publisherName имя автора статьи
 * @param publishDate дата публикации
 * @param title название статьи
 */
public record ArticleInfoDTO(Long articleId, String publisherName, ZonedDateTime publishDate, String title) {
}
