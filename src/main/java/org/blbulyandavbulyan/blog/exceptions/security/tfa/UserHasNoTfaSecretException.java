package org.blbulyandavbulyan.blog.exceptions.security.tfa;

import org.blbulyandavbulyan.blog.exceptions.BadRequestException;

public class UserHasNoTfaSecretException extends BadRequestException {
    public UserHasNoTfaSecretException(String username) {
        super("User '" + username + "' doesn't have TFA secret!");
    }
}
