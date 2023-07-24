package org.blbulyandavbulyan.blog.dtos.comment;

import java.time.ZonedDateTime;

public record CommentDto(Long commentId, String authorName, String text, ZonedDateTime publishDate) {
}
