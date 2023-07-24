package org.blbulyandavbulyan.blog.dtos.user;

import java.util.List;

/**
 * Данная запись будет использоваться для создания пользователей администраторами
 * @param name имя пользователя
 * @param password пароль
 * @param roleNames список имён ролей
 */
public record UserCreateRequest(String name, String password, List<String> roleNames) {
}
