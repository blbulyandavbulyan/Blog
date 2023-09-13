package org.blbulyandavbulyan.blog.dtos.roles;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.blbulyandavbulyan.blog.annotations.validation.user.ValidUserId;

import java.util.List;

/**
 * Запрос на обновления ролей у пользователя
 * @param userId ИД пользователя, роли которого нужно обновить
 * @param rolesNames список ролей, которые будут вместо старых
 */
public record UpdateRolesDto(@ValidUserId Long userId, @NotNull List<@NotBlank String> rolesNames) {
}
