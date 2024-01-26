package org.blbulyandavbulyan.blog.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TokenServiceParseException extends RuntimeException{
    public TokenServiceParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
