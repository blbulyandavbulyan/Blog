package org.blbulyandavbulyan.blog.dtos.comment;

import org.blbulyandavbulyan.blog.annotations.validation.comment.ValidCommentText;

public record EditCommentRequest(@ValidCommentText String text) {
}
