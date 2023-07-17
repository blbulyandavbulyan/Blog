package org.blbulyandavbulyan.blog.exceptions.handling;

import org.blbulyandavbulyan.blog.exceptions.AppError;
import org.blbulyandavbulyan.blog.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionController {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<AppError> processUserNotFoundException(UserNotFoundException e){
        return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), e.getMessage()), HttpStatus.UNAUTHORIZED);
    }
}
