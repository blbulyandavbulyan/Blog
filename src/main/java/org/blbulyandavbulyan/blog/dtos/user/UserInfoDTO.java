package org.blbulyandavbulyan.blog.dtos.user;

import java.util.List;

/**
 * Данный интерфейс предоставляет DTO для информации о пользователе, получаемой администратором
 */
public interface UserInfoDTO {
    /**
     * Получает ИД пользователя
     * @return ИД пользователя
     */

    Long getUserId();

    /**
     * Получает имя пользователя
     * @return имя пользователя
     */

    String getName();

    /**
     * Получает спиcок ролей данного пользователя
     * @return  список ролей пользователя
     */

    List<RoleDto> getRoles();

    /**
     * DTO для роли пользователя
     */
    interface RoleDto{
        /**
         * Получает имя данной роли
         * @return имя данной роли
         */
        String getName();
    }

}
