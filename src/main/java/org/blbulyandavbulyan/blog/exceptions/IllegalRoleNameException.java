package org.blbulyandavbulyan.blog.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Данное исключение бросается при создании пользователя администратором, если указано неверное имя роли
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalRoleNameException extends RuntimeException{
    public IllegalRoleNameException(String message) {
        super(message);
    }
}
