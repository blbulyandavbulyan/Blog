package org.blbulyandavbulyan.blog.dtos.article;

/**
 * DTO для представления базовой информации о статье, для отправки клиенту
 * @param articleId ИД статьи
 * @param title название статьи
 */
public record ArticleInfoDTO(Long articleId, String title) {
}
