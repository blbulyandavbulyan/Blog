package org.blbulyandavbulyan.blog.exceptions;

import org.springframework.http.HttpStatus;

public class TOTPQrCodeGenerationException extends BlogException{
    public TOTPQrCodeGenerationException(Throwable cause) {
        super("Error during generating TOTP qr code", cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
