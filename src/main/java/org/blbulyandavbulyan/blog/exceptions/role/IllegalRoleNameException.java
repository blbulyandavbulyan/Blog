package org.blbulyandavbulyan.blog.exceptions.role;

import org.blbulyandavbulyan.blog.exceptions.BadRequestException;

/**
 * Данное исключение бросается при создании пользователя администратором, если указано неверное имя роли
 */
public class IllegalRoleNameException extends BadRequestException {
    public IllegalRoleNameException(String message) {
        super(message);
    }
}
