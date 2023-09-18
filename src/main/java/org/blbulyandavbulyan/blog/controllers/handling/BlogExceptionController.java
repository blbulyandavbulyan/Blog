package org.blbulyandavbulyan.blog.controllers.handling;

import org.blbulyandavbulyan.blog.dtos.error.AppError;
import org.blbulyandavbulyan.blog.exceptions.BlogException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BlogExceptionController {
    @ExceptionHandler(BlogException.class)
    public ResponseEntity<?> processException(BlogException e){
        return ResponseEntity.status(e.getHttpStatus()).body(new AppError(e.getHttpStatus().value(), e.getMessage()));
    }
}
