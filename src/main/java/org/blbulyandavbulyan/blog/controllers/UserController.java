package org.blbulyandavbulyan.blog.controllers;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.authorization.RegistrationUser;
import org.blbulyandavbulyan.blog.exceptions.AppError;
import org.blbulyandavbulyan.blog.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationUser registrationUser){
        if(!userService.exists(registrationUser.username())){//если пользователя не существует, можем регистрировать
            userService.registerUser(registrationUser.username(), registrationUser.password());
            return new ResponseEntity<>("user was successfully registered!", HttpStatus.CREATED);
        }
        else return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "User already registered!"), HttpStatus.BAD_REQUEST);
    }
}
