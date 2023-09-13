package org.blbulyandavbulyan.blog.dtos.article;

import org.blbulyandavbulyan.blog.annotations.validation.article.ValidArticleText;
import org.blbulyandavbulyan.blog.annotations.validation.article.ValidArticleTitle;

/**
 * Данная предназначена для приёма от пользователя статьи для публикации
 * @param title название статьи
 * @param text текст статьи
 */
public record ArticleForPublishing(@ValidArticleTitle String title, @ValidArticleText String text) {
}
