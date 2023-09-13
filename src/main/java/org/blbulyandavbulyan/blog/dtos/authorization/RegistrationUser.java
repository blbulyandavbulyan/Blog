package org.blbulyandavbulyan.blog.dtos.authorization;

import org.blbulyandavbulyan.blog.annotations.validation.user.ValidRawPassword;
import org.blbulyandavbulyan.blog.annotations.validation.user.ValidUsername;

/**
 * Данная запись предназначена для запроса регистрации пользователя, приходит на сервер в формате json
 * @param username имя нового пользователя
 * @param password пароль
 */
public record RegistrationUser (@ValidUsername String username, @ValidRawPassword String password){
}
