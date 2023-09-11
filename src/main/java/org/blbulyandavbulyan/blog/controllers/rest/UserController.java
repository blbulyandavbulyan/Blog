package org.blbulyandavbulyan.blog.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.annotations.validation.user.ValidUserId;
import org.blbulyandavbulyan.blog.dtos.authorization.RegistrationUser;
import org.blbulyandavbulyan.blog.dtos.roles.UpdateRolesDto;
import org.blbulyandavbulyan.blog.dtos.user.UserCreateRequest;
import org.blbulyandavbulyan.blog.dtos.user.UserCreatedResponse;
import org.blbulyandavbulyan.blog.dtos.user.UserInfoDTO;
import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для управления пользователями
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    /**
     * Сервис для управления пользователями
     */
    private final UserService userService;

    /**
     * Метод обрабатывает запрос о регистрации пользователя
     * @param registrationUser запрос на регистрацию пользователя, с необходимыми данными
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@Validated @RequestBody RegistrationUser registrationUser) {
        userService.registerUser(registrationUser.username(), registrationUser.password());
    }

    /**
     * Обрабатывает запрос на удаления пользователя(доступно только админам)
     * @param id ИД пользователя, которого нужно удалить
     */
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserById(@ValidUserId @PathVariable Long id) {
        userService.deleteById(id);
    }
    /**
     * Создаёт пользователя с заданными параметрами
     */
    @Secured("ROLE_ADMIN")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserCreatedResponse createUser(@Validated @RequestBody UserCreateRequest userCreateRequest){
        User user = userService.createUser(userCreateRequest);
        return new UserCreatedResponse(user.getUserId());
    }

    /**
     * Метод получает информацию о пользователя для администраторов
     * @param id ИД пользователя, о котором нужно узнать информацию
     * @return информацию о пользователе для администратора
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/{id}")
    public UserInfoDTO getUserInfo(@ValidUserId @PathVariable Long id){
        return userService.getUserInfo(id);
    }

    /**
     * Ищет информацию о пользователях в виде страницы
     * @param pageSize размер страницы
     * @param pageNumber номер страницы
     * @param requestParams multimap, содержащая параметры запроса, и из которой будут доставаться параметры фильтрации
     * @return искомую страницу с информацией
     */
    @Secured("ROLE_ADMIN")
    @GetMapping
    public Page<UserInfoDTO> getUserInfos(@RequestParam(defaultValue = "5", name = "s") Integer pageSize, @RequestParam(defaultValue = "1", name = "p") Integer pageNumber, @RequestParam MultiValueMap<String, String> requestParams){
        return userService.getUserInfos(requestParams, pageNumber - 1, pageSize);
    }
    /**
     * Обновляет привилегии у пользователя
     * @param updateRolesDto DTO содержащее ИД пользователя, роли которого нужно обновить и набор новых ролей
     */
    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/roles")
    public void updateUserPrivileges(@Validated @RequestBody UpdateRolesDto updateRolesDto){
        userService.updateRoles(updateRolesDto.userId(), updateRolesDto.rolesNames());
    }
}
