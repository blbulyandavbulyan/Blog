package org.blbulyandavbulyan.blog.controllers;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.JWTTokenUtils;
import org.blbulyandavbulyan.blog.dtos.authorization.JwtRequest;
import org.blbulyandavbulyan.blog.dtos.authorization.JwtResponse;
import org.blbulyandavbulyan.blog.dtos.authorization.RegistrationUser;
import org.blbulyandavbulyan.blog.exceptions.AppError;
import org.blbulyandavbulyan.blog.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JWTTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest){
        try {
            var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));
            UserDetails userDetails = userService.loadUserByUsername(authRequest.username());
            return ResponseEntity.ok(new JwtResponse(jwtTokenUtils.generateToken(userDetails)));
        }
        catch (BadCredentialsException e){
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Incorrect username or password!"), HttpStatus.UNAUTHORIZED);
        }
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationUser registrationUser){
        if(!userService.exists(registrationUser.username())){//если пользователя не существует, можем регистрировать
            userService.registerUser(registrationUser.username(), registrationUser.password());
            return new ResponseEntity<>("user was successfully registered!", HttpStatus.CREATED);
        }
        else return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "User already registered!"), HttpStatus.BAD_REQUEST);
    }
}
