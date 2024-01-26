package org.blbulyandavbulyan.blog.dtos.authorization;

import org.blbulyandavbulyan.blog.annotations.validation.user.ValidRawPassword;
import org.blbulyandavbulyan.blog.annotations.validation.user.ValidUsername;

/**
 * Данная запись предназначена для начала аутентификации(первый этап)
 * @param username имя пользователя
 * @param password пароль
 */
public record AuthenticationRequest(@ValidUsername String username, @ValidRawPassword String password){
}
