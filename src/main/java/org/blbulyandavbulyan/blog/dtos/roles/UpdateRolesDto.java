package org.blbulyandavbulyan.blog.dtos.roles;


import java.util.List;

/**
 * Запрос на обновления ролей у пользователя
 * @param userId ИД пользователя, роли которого нужно обновить
 * @param rolesNames список ролей, которые будут вместо старых
 */
public record UpdateRolesDto(Long userId, List<String> rolesNames) {
}
