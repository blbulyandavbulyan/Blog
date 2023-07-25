package org.blbulyandavbulyan.blog.exceptions;

/**
 * Общий класс исключений для моего приложения
 */
public class BlogException extends RuntimeException{
    public BlogException() {
    }
    public BlogException(String message) {
        super(message);
    }

    public BlogException(String message, Throwable cause) {
        super(message, cause);
    }
}
