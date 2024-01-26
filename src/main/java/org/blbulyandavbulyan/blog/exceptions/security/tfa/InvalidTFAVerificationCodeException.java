package org.blbulyandavbulyan.blog.exceptions.security.tfa;

import org.blbulyandavbulyan.blog.exceptions.BlogException;
import org.springframework.http.HttpStatus;

public class InvalidTFAVerificationCodeException extends BlogException {
    public InvalidTFAVerificationCodeException(HttpStatus httpStatus) {
        super("Invalid TFA verification code!", httpStatus);
    }
}
