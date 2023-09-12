package org.blbulyandavbulyan.blog.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.blbulyandavbulyan.blog.annotations.validation.user.ValidRawPassword;
import org.blbulyandavbulyan.blog.annotations.validation.user.ValidUsername;

import java.util.List;

/**
 * Данная запись будет использоваться для создания пользователей администраторами
 * @param name имя пользователя
 * @param password пароль
 * @param roleNames список имён ролей
 */
public record UserCreateRequest(@ValidUsername String name, @ValidRawPassword String password, @NotNull List<@NotBlank String> roleNames) {
}
