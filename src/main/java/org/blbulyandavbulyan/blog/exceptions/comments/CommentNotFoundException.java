package org.blbulyandavbulyan.blog.exceptions.comments;

import org.blbulyandavbulyan.blog.exceptions.BlogException;

public class CommentNotFoundException extends BlogException {
    public CommentNotFoundException(String message) {
        super(message);
    }
}
