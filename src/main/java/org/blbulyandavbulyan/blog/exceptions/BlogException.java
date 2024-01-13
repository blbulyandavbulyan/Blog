package org.blbulyandavbulyan.blog.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Общий класс исключений для моего приложения
 */
@Getter
public abstract class BlogException extends RuntimeException{
    private final HttpStatus httpStatus;
    public BlogException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public BlogException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
}
