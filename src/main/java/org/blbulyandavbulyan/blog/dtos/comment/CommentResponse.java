package org.blbulyandavbulyan.blog.dtos.comment;

import java.time.ZonedDateTime;

/**
 * DTO для комментария, содержащее всю необходимую информацию для отображения
 * @param id ИД комментария
 * @param authorName имя автора
 * @param text текст комментария
 * @param publishDate дата публикации
 */
public record CommentResponse(Long id, String authorName, String text, ZonedDateTime publishDate) {
}
