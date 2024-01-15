package org.blbulyandavbulyan.blog.services;

import org.blbulyandavbulyan.blog.dtos.authorization.AuthenticationRequest;
import org.blbulyandavbulyan.blog.dtos.authorization.AuthenticationResponse;
import org.blbulyandavbulyan.blog.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = AuthenticationServiceTest.class)
@Import(AuthenticationService.class)
class AuthenticationServiceTest {
    @MockBean
    private UserService userService;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean(name = "tokenService")
    private TokenService tokenService;
    @MockBean
    private TOTPService totpService;
    @MockBean(name = "secondStepTokenService")
    private TokenService secondStepMFATokenService;
    @Autowired
    private AuthenticationService underTest;

    @DisplayName("authenticate if first step of authentication failed(AuthenticationManager threw an exception)")
    @ParameterizedTest(name = "exception thrown = {0}")
    @ValueSource(classes = {BadCredentialsException.class, LockedException.class, DisabledException.class})
    void authenticateTestIfFirstStepOfAuthenticationFailed(Class<? extends AuthenticationException> authenticationException) {
        when(authenticationManager.authenticate(any())).thenThrow(authenticationException);
        assertThrows(authenticationException, () -> underTest.authenticate(new AuthenticationRequest("test", "test")));
        verifyNoInteractions(userService, tokenService, secondStepMFATokenService, totpService);
    }

    @ParameterizedTest(name = "tfa enabled = {0}, user has tfa secret = {1}")
    @CsvSource({"true,true", "true,false", "false,false", "false,true"})
    void authenticateIfFirstStepWasPassed(boolean tfaEnabled, boolean userHasTfaSecret) {
        var authenticationRequest = new AuthenticationRequest("testusername", "testpassword");
        String username = authenticationRequest.username();
        User user = mock(User.class);
        when(user.isTfaEnabled()).thenReturn(tfaEnabled);
        if (tfaEnabled) {
            when(user.getTfaSecret()).thenReturn(userHasTfaSecret ? "secret" : null);
        }
        when(userService.findByName(username)).thenReturn(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, authenticationRequest.password());
        when(authenticationManager.authenticate(usernamePasswordAuthenticationToken)).thenReturn(authentication);
        if (tfaEnabled && userHasTfaSecret) {
            String expectedToken = "seondstepmfatoken";
            when(secondStepMFATokenService.generateToken(username)).thenReturn(expectedToken);
            var expectedAuthenticationResponse = new AuthenticationResponse(expectedToken, true);
            var actualAuthenticationResponse = assertDoesNotThrow(()->underTest.authenticate(authenticationRequest));
            verifyNoInteractions(tokenService);
            assertEquals(expectedAuthenticationResponse, actualAuthenticationResponse);
        } else {
            Collection<? extends GrantedAuthority> authorities = List.of();
            String expectedToken = "realtoken";
            when(tokenService.generateToken(username, authorities)).thenReturn(expectedToken);
            when(authentication.getAuthorities()).thenAnswer(x->authorities);
            var expectedAuthenticationResponse = new AuthenticationResponse(expectedToken, false);
            var authenticationResponse = assertDoesNotThrow(() -> underTest.authenticate(authenticationRequest));
            verifyNoInteractions(secondStepMFATokenService);
            assertEquals(expectedAuthenticationResponse, authenticationResponse);
        }
        verifyNoInteractions(totpService);
    }
    //TODO 15.01.2024: написать тесты на verifyAuth метод
}