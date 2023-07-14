package org.blbulyandavbulyan.blog.exceptions;

public class UserNotFoundException extends ResourceNofFoundException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
