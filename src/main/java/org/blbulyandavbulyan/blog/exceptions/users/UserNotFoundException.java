package org.blbulyandavbulyan.blog.exceptions.users;

import org.blbulyandavbulyan.blog.exceptions.ResourceNofFoundException;

public class UserNotFoundException extends ResourceNofFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
