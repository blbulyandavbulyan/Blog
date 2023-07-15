package org.blbulyandavbulyan.blog.dtos.authorization;

/**
 * Данная запись предназначена для запроса регистрации пользователя, приходит на сервер в формате json
 * @param username имя нового пользователя
 * @param password пароль
 */
public record RegistrationUser (String username, String password){
}
