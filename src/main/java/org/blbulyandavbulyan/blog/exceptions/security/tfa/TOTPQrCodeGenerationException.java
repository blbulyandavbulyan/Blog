package org.blbulyandavbulyan.blog.exceptions.security.tfa;

import org.blbulyandavbulyan.blog.exceptions.BlogException;
import org.springframework.http.HttpStatus;

public class TOTPQrCodeGenerationException extends BlogException {
    public TOTPQrCodeGenerationException(Throwable cause) {
        super("Error during generating TOTP qr code", cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
