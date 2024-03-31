package org.blbulyandavbulyan.blog.controllers.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.authorization.RegistrationUser;
import org.blbulyandavbulyan.blog.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "registration")
public class RegistrationController {
    private final UserService userService;
    /**
     * Метод обрабатывает запрос о регистрации пользователя
     * @param registrationUser запрос на регистрацию пользователя, с необходимыми данными
     */
    @PostMapping("/api/v1/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@Validated @RequestBody RegistrationUser registrationUser) {
        userService.registerUser(registrationUser.username(), registrationUser.password());
    }
}
