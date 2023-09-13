package org.blbulyandavbulyan.blog.dtos.comment;

import java.time.ZonedDateTime;

/**
 * DTO для комментария, содержащее всю необходимую информацию для отображения
 * @param commentId ИД комментария
 * @param authorName имя автора
 * @param text текст комментария
 * @param publishDate дата публикации
 */
public record CommentResponse(Long commentId, String authorName, String text, ZonedDateTime publishDate) {
}
