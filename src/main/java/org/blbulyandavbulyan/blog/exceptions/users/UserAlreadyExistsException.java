package org.blbulyandavbulyan.blog.exceptions.users;

import org.blbulyandavbulyan.blog.exceptions.BlogException;

/**
 * Данное исключение бросается в случае если на момент регистрации/создания пользователь уже существовал
 */
public class UserAlreadyExistsException extends BlogException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
