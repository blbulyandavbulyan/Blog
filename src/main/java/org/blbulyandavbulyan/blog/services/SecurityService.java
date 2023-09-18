package org.blbulyandavbulyan.blog.services;

import org.blbulyandavbulyan.blog.exceptions.security.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Данный сервис предназначен для более удобной работы со spring безопасностью
 */
@Service
public class SecurityService {
    /**
     * Данный метод запускает переданную функцию, если имя цели совпадает с именем исполнителя или исполнитель админ
     * @param authentication объект authentication, содержащий имя пользователя и роли
     * @param targetUsername имя целевого пользователя
     * @param r функция, которую будет запускать данный метод
     * @throws AccessDeniedException если исполнитель не равен цели и он не админ
     */
    public void executeIfExecutorIsAdminOrEqualToTarget(Authentication authentication, String targetUsername, Runnable r){
        if(targetUsername.equals(authentication.getName())
                || authentication.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"))) {
            r.run();
        }
        else throw new AccessDeniedException();
    }
}
