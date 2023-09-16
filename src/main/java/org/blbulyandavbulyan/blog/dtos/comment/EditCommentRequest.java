package org.blbulyandavbulyan.blog.dtos.comment;

import org.blbulyandavbulyan.blog.annotations.validation.comment.ValidCommentId;
import org.blbulyandavbulyan.blog.annotations.validation.comment.ValidCommentText;

public record EditCommentRequest(@ValidCommentId Long commentId, @ValidCommentText String text) {
}
