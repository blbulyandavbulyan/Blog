package org.blbulyandavbulyan.blog.dtos.user;

/**
 * Данный класс предоставляет DTO для обновления пароля пользователя
 * @param username имя пользователя, для которого будет обновлён пароль
 * @param password новый пароль
 */
public record UpdateUserPasswordRequest(String username, String password) {
}
