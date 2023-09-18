package org.blbulyandavbulyan.blog.exceptions.security;

import org.blbulyandavbulyan.blog.exceptions.BlogException;
import org.springframework.http.HttpStatus;

public class AccessDeniedException extends BlogException {
    public AccessDeniedException() {
        super("Operation not permitted", HttpStatus.FORBIDDEN);
    }
}
