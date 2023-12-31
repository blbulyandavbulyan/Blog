package org.blbulyandavbulyan.blog.services;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.entities.Role;
import org.blbulyandavbulyan.blog.exceptions.role.IllegalRoleNameException;
import org.blbulyandavbulyan.blog.repositories.RoleRepository;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с ролями
 */
@Service
@RequiredArgsConstructor
public class RoleService {
    /**
     * Репозиторий для работы с ролями
     */
    private final RoleRepository roleRepository;

    /**
     * Получает ссылку на роль по имени роли
     * @param roleName имя роли, которую нужно получить
     * @return найденную ссылку на роль
     * @throws IllegalRoleNameException если роль не найдена
     */
    public Role getReferenceByRoleName(String roleName){
        return roleRepository.getReferenceByName(roleName).orElseThrow(()-> new IllegalRoleNameException("role with name " + roleName + " not found!"));
    }

    /**
     * Проверяет, существует ли роль с заданным именем
     * @param roleName имя роли, которую нужно проверить на существование
     * @return true если существует
     */
    public boolean existsByRoleName(String roleName){
        return roleRepository.existsByName(roleName);
    }
}
