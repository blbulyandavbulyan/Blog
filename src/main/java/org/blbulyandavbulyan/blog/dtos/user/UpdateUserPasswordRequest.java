package org.blbulyandavbulyan.blog.dtos.user;

import org.blbulyandavbulyan.blog.annotations.validation.user.ValidRawPassword;
import org.blbulyandavbulyan.blog.annotations.validation.user.ValidUsername;

/**
 * Данный класс предоставляет DTO для обновления пароля пользователя
 * @param username имя пользователя, для которого будет обновлён пароль
 * @param password новый пароль
 */
public record UpdateUserPasswordRequest(@ValidUsername String username, @ValidRawPassword String password) {
}
