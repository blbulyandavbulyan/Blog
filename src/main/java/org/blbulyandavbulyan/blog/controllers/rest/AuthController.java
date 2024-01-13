package org.blbulyandavbulyan.blog.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.authorization.AuthenticationRequest;
import org.blbulyandavbulyan.blog.dtos.authorization.AuthenticationResponse;
import org.blbulyandavbulyan.blog.services.AuthenticationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер обрабатывающий запросы об авторизации
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    /**
     * Метод создаёт токен для пользователя, в случае успешного прохождения авторизации
     *
     * @param authRequest запрос содержащий логин и пароль
     * @return ответ, содержащий JWT токен в случае успешной авторизации
     */
    @PostMapping
    public AuthenticationResponse createAuthToken(@Validated @RequestBody AuthenticationRequest authRequest){
        return authenticationService.authenticate(authRequest);
    }
}
