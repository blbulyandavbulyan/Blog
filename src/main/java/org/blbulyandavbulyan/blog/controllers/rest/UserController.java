package org.blbulyandavbulyan.blog.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.authorization.RegistrationUser;
import org.blbulyandavbulyan.blog.dtos.user.UserCreateRequest;
import org.blbulyandavbulyan.blog.dtos.user.UserInfoDTO;
import org.blbulyandavbulyan.blog.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.MultiValueMap;
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
    public void registerUser(@RequestBody RegistrationUser registrationUser) {
        userService.registerUser(registrationUser.username(), registrationUser.password());
    }

    /**
     * Обрабатывает запрос на удаления пользователя(доступно только админам)
     * @param id ИД пользователя, которого нужно удалить
     */
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserById(@PathVariable Long id) {
        userService.deleteById(id);
    }
    /**
     * Создаёт пользователя с заданными параметрами
     */
    @Secured("ROLE_ADMIN")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody UserCreateRequest userCreateRequest){
        userService.createUser(userCreateRequest);
    }

    /**
     * Метод получает информацию о пользователя для администраторов
     * @param id ИД пользователя, о котором нужно узнать информацию
     * @return информацию о пользователе для администратора
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/{id}")
    public UserInfoDTO getUserInfo(@PathVariable Long id){
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
        return userService.getUserInfos(requestParams, pageNumber, pageSize);
    }
}
