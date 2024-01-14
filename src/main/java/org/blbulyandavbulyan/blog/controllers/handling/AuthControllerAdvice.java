package org.blbulyandavbulyan.blog.controllers.handling;

import org.blbulyandavbulyan.blog.controllers.rest.AuthController;
import org.blbulyandavbulyan.blog.dtos.error.AppError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = AuthController.class)
public class AuthControllerAdvice {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> processBadCredential(BadCredentialsException processedException){
        return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), processedException.getMessage()), HttpStatus.UNAUTHORIZED);
    }
}
