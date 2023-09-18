package org.blbulyandavbulyan.blog.exceptions;

/**
 * Данное исключение бросается при создании пользователя администратором, если указано неверное имя роли
 */
public class IllegalRoleNameException extends BadRequestException{
    public IllegalRoleNameException(String message) {
        super(message);
    }
}
