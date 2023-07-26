package org.blbulyandavbulyan.blog.repositories;

import org.blbulyandavbulyan.blog.entities.Role;
import org.springframework.data.repository.Repository;

import java.util.Optional;

/**
 * Репозиторий ролей<br>
 * Не имеет модифицирующих методов, т.к. имеющиеся роли нельзя модифицировать
 */
public interface RoleRepository extends Repository<Role, Long> {
    /**
     * Проверяет, существует ли роль по имени
     * @param roleName имя роли, которую нужно проверить на существование
     * @return true если существует
     */
    boolean existsByName(String roleName);

    /**
     * Получает ссылку на роль по её имени
     * @param roleName имя роли
     * @return искомая ссылка на роль
     */
    Optional<Role> getReferenceByName(String roleName);
}