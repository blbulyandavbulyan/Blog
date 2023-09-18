package org.blbulyandavbulyan.blog.exceptions;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends BlogException{
    public AccessDeniedException() {
        super("Operation not permitted", HttpStatus.FORBIDDEN);
    }
}
