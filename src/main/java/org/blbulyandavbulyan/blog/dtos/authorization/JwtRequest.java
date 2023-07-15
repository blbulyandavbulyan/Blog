package org.blbulyandavbulyan.blog.dtos.authorization;

/**
 * Данная запись предназначена для запроса JWT токена, для авторизации, приходит на сервер в формате JSON
 * @param username имя пользователя
 * @param password пароль
 */
public record JwtRequest (String username, String password){
}
