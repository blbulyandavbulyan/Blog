package org.blbulyandavbulyan.blog.services;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.exceptions.security.tfa.InvalidTFAVerificationCodeException;
import org.blbulyandavbulyan.blog.exceptions.security.tfa.TFASetupWasNotBeganException;
import org.blbulyandavbulyan.blog.exceptions.security.tfa.TfaAlreadyEnabledException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TOTPSetupService {
    private final TOTPService totpService;
    private final UserService userService;
    private static boolean isTfaRequired(User user) {
        return user.isTfaEnabled() && user.getTfaSecret() != null;
    }

    private void throwIfInvalidTOTPCode(String secret, String code) {
        if (!totpService.verifyCode(secret, code)) {
            throw new InvalidTFAVerificationCodeException(HttpStatus.BAD_REQUEST);
        }
    }
    @Transactional
    public String beginSetupTFA(String username) {
        User user = userService.findByName(username);
        if (isTfaRequired(user)) {
            throw new TfaAlreadyEnabledException();
        }
        user.setTfaSecret(totpService.generateNewSecret());
        return totpService.generateQrCodeImageUri(user.getTfaSecret());
    }

    @Transactional
    public void finishTFASetup(String username, String code) {
        User user = userService.findByName(username);
        String tfaSecret = Optional.ofNullable(user.getTfaSecret()).orElseThrow(TFASetupWasNotBeganException::new);
        throwIfInvalidTOTPCode(tfaSecret, code);
        user.setTfaEnabled(true);
    }
    @Transactional
    public void disableTFA(String username, String code) {
        User user = userService.findByName(username);
        if (isTfaRequired(user)) {
            throwIfInvalidTOTPCode(user.getTfaSecret(), code);
            user.setTfaEnabled(false);
            user.setTfaSecret(null);
        }
    }
    public boolean isTfaEnabled(String username) {
        return userService.isTfaEnabled(username);
    }
}
