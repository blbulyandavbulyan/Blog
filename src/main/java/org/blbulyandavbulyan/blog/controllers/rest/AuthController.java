package org.blbulyandavbulyan.blog.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.authorization.JwtRequest;
import org.blbulyandavbulyan.blog.dtos.authorization.JwtResponse;
import org.blbulyandavbulyan.blog.exceptions.AppError;
import org.blbulyandavbulyan.blog.services.UserService;
import org.blbulyandavbulyan.blog.utils.JWTTokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер обрабатывающий запросы об авторизации
 */
@RestController
@RequiredArgsConstructor
public class AuthController {
    /**
     * Сервис пользователей
     */
    private final UserService userService;
    /**
     * Ссылка на утилитный класс для управления jwt токенами
     */
    private final JWTTokenUtils jwtTokenUtils;
    /**
     * Ссылка на AuthenticationManager для управления аутентификацией
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Метод создаёт токен для пользователя, в случае успешного прохождения авторизации
     * @param authRequest запрос содержащий логин и пароль
     * @return ответ, содержащий JWT токен в случае успешной авторизации
     */
    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));
            UserDetails userDetails = userService.loadUserByUsername(authRequest.username());
            return ResponseEntity.ok(new JwtResponse(jwtTokenUtils.generateToken(userDetails)));
        }
        catch (BadCredentialsException e){
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Incorrect username or password!"), HttpStatus.UNAUTHORIZED);
        }
    }
}
