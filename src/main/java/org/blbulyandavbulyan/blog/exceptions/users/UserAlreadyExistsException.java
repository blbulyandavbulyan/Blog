package org.blbulyandavbulyan.blog.exceptions.users;

import org.blbulyandavbulyan.blog.exceptions.BadRequestException;

/**
 * Данное исключение бросается в случае если на момент регистрации/создания пользователь уже существовал
 */
public class UserAlreadyExistsException extends BadRequestException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
