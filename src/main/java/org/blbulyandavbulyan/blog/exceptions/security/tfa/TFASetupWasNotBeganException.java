package org.blbulyandavbulyan.blog.exceptions.security.tfa;

import org.blbulyandavbulyan.blog.exceptions.BlogException;
import org.springframework.http.HttpStatus;

public class TFASetupWasNotBeganException extends BlogException {
    public TFASetupWasNotBeganException() {
        super("Impossible to complete TFA setup, because it was not began", HttpStatus.BAD_REQUEST);
    }
}
