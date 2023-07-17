package org.blbulyandavbulyan.blog.exceptions.users;

import org.blbulyandavbulyan.blog.exceptions.BlogException;

public class UserAlreadyExistsException extends BlogException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
