package org.blbulyandavbulyan.blog.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.authorization.JwtRequest;
import org.blbulyandavbulyan.blog.dtos.authorization.JwtResponse;
import org.blbulyandavbulyan.blog.exceptions.AppError;
import org.blbulyandavbulyan.blog.utils.JWTTokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    @PostMapping
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest){
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));
            return ResponseEntity.ok(new JwtResponse(jwtTokenUtils.generateToken(authentication.getName(), authentication.getAuthorities())));
        }
        catch (BadCredentialsException e){
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Incorrect username or password!"), HttpStatus.UNAUTHORIZED);
        }
    }
}
