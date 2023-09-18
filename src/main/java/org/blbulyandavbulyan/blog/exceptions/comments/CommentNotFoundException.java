package org.blbulyandavbulyan.blog.exceptions.comments;

import org.blbulyandavbulyan.blog.exceptions.ResourceNofFoundException;

public class CommentNotFoundException extends ResourceNofFoundException {
    public CommentNotFoundException(String message) {
        super(message);
    }
}
