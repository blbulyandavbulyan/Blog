package org.blbulyandavbulyan.blog.dtos.authorization;

import org.blbulyandavbulyan.blog.annotations.validation.user.ValidRawPassword;
import org.blbulyandavbulyan.blog.annotations.validation.user.ValidUsername;

/**
 * Данная запись предназначена для запроса JWT токена, для авторизации, приходит на сервер в формате JSON
 * @param username имя пользователя
 * @param password пароль
 */
public record JwtRequest (@ValidUsername String username, @ValidRawPassword String password){
}
