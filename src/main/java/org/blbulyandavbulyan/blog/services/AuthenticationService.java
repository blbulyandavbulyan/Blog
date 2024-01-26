package org.blbulyandavbulyan.blog.services;

import org.blbulyandavbulyan.blog.dtos.authorization.AuthenticationRequest;
import org.blbulyandavbulyan.blog.dtos.authorization.AuthenticationResponse;
import org.blbulyandavbulyan.blog.dtos.authorization.VerificationRequest;
import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.exceptions.TokenServiceParseException;
import org.blbulyandavbulyan.blog.exceptions.security.tfa.UserHasNoTfaSecretException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final TOTPService totpService;
    private final TokenService secondStepMFATokenService;

    public AuthenticationService(TOTPService totpService, UserService userService, AuthenticationManager authenticationManager,
                                 @Qualifier("tokenService") TokenService tokenService,
                                 @Qualifier("secondStepTokenService") TokenService secondStepMFATokenService) {
        this.totpService = totpService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.secondStepMFATokenService = secondStepMFATokenService;
    }
    @Transactional(readOnly = true)
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.username(), authenticationRequest.password()));
        User user = userService.findByName(authenticationRequest.username());
        if (user.isTfaEnabled() && user.getTfaSecret() != null) {
            return new AuthenticationResponse(secondStepMFATokenService.generateToken(authentication.getName()), true);
        }
        else {
            return new AuthenticationResponse(tokenService.generateToken(authentication.getName(), authentication.getAuthorities()), false);
        }
    }
    public AuthenticationResponse verifyAuth(VerificationRequest verificationRequest) {
        try {
            String username = secondStepMFATokenService.getUserName(verificationRequest.jwtToken());
            User user = userService.findByName(username);
            String tfaSecret = Optional.ofNullable(user.getTfaSecret())
                    .orElseThrow(()-> new UserHasNoTfaSecretException(username));
            if (totpService.isNotValidCode(tfaSecret, verificationRequest.code())) {
                throw new BadCredentialsException("Invalid verification code!");
            }
            return new AuthenticationResponse(tokenService.generateToken(user.getName(), user.getAuthorities()), false);
        } catch (TokenServiceParseException exception) {
            throw new BadCredentialsException("Invalid token!", exception);
        }
    }
}
