package org.blbulyandavbulyan.blog.exceptions.users;

import org.blbulyandavbulyan.blog.exceptions.ResourceNofFoundException;

/**
 * Данное исключение бросается, в случае если пользователь не был найден по ИД или имени
 */
public class UserNotFoundException extends ResourceNofFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
