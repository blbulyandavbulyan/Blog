package org.blbulyandavbulyan.blog.exceptions.security.tfa;

import org.blbulyandavbulyan.blog.exceptions.BlogException;
import org.springframework.http.HttpStatus;

public class TfaAlreadyEnabledException extends BlogException {
    public TfaAlreadyEnabledException() {
        super("Disable TFA before configuring new", HttpStatus.BAD_REQUEST);
    }
}
