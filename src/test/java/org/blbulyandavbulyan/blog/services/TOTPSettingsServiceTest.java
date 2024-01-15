package org.blbulyandavbulyan.blog.services;

import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.exceptions.security.tfa.InvalidTFAVerificationCodeException;
import org.blbulyandavbulyan.blog.exceptions.security.tfa.TFASetupWasNotBeganException;
import org.blbulyandavbulyan.blog.exceptions.security.tfa.TfaAlreadyEnabledException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TOTPSettingsServiceTest {
    @Mock
    private TOTPService totpService;
    @Mock
    private UserService userService;
    @InjectMocks
    private TOTPSettingsService underTest;

    @ParameterizedTest(name = "check {0}")
    @ValueSource(strings = {"beginSetupTFA", "finishTFASetup", "disableTFA"})
    void checkTransactionalMethods(String methodName) {
        Method foundMethod = Arrays.stream(TOTPSettingsService.class.getMethods())
                .filter(method -> method.getName().equals(methodName))
                .findFirst()
                .or(() -> fail("Method with name " + methodName + " not found in class"))
                .get();
        assertTrue(foundMethod.isAnnotationPresent(Transactional.class),
                "Transactional annotation not present in method " + methodName);
    }

    @ParameterizedTest(name = "tfaEnabled = {0}, userHasSecret = {1}")
    @CsvSource({"true, true", "true, false", "false, true", "false, false"})
    void beginSetupTFATest(boolean tfaEnabled, boolean userHasSecret) {
        String username = "testusername";
        User user = mock(User.class);
        when(userService.findByName(username)).thenReturn(user);
        when(user.isTfaEnabled()).thenReturn(tfaEnabled);
        if(tfaEnabled) {
            when(user.getTfaSecret()).thenReturn(userHasSecret ? "secret" : null);
        }
        if (tfaEnabled && userHasSecret) {
            assertThrows(TfaAlreadyEnabledException.class, () -> underTest.beginSetupTFA(username));
            verifyNoMoreInteractions(userService, totpService, user);
        } else {
            String expectedTfaSecret = "newsecret";
            String expectedQrCode = "expectedqrcode";
            when(totpService.generateNewSecret()).thenReturn(expectedTfaSecret);
            when(totpService.generateQrCodeImageUri(expectedTfaSecret, username)).thenReturn(expectedQrCode);
            String actualQrCode = assertDoesNotThrow(() -> underTest.beginSetupTFA(username));
            assertEquals(expectedQrCode, actualQrCode);
            verify(user, times(1)).setTfaSecret(expectedTfaSecret);
            verifyNoMoreInteractions(totpService, userService, user);
        }
    }

    @Test
    void finishTfaSetupWhenUserDoesNotHaveSecretTest() {
        String username = "testusername";
        String code = "code214124";
        User user = new User();
        when(userService.findByName(username)).thenReturn(user);
        assertThrows(TFASetupWasNotBeganException.class, () -> underTest.finishTFASetup(username, code));
    }
    @ParameterizedTest(name = "valid code = {0}")
    @ValueSource(booleans = {true, false})
    void finishTFASetupWhenUserHasSecretTest(boolean validCode) {
        String username = "testusername";
        String code = "code214124";
        String secret = "secret";
        User user = mock(User.class);
        when(userService.findByName(username)).thenReturn(user);
        when(user.getTfaSecret()).thenReturn(secret);
        when(totpService.isNotValidCode(secret, code)).thenReturn(!validCode);
        Executable executable = () -> underTest.finishTFASetup(username, code);
        if (validCode) {
            assertDoesNotThrow(executable);
            verify(user, times(1)).setTfaEnabled(true);
            verifyNoMoreInteractions(user, totpService, userService);
        } else {
            assertThrows(InvalidTFAVerificationCodeException.class, executable);
            verify(user, never()).setTfaEnabled(anyBoolean());
        }
    }

    @ParameterizedTest(name = "tfa enabled = {0}")
    @ValueSource(booleans = {true, false})
    void isTfaEnabledTest(boolean tfaEnabled) {
        String username = "testusername";
        when(userService.isTfaEnabled(username)).thenReturn(tfaEnabled);
        assertEquals(tfaEnabled, underTest.isTfaEnabled(username));
        verifyNoMoreInteractions(userService, totpService);
    }
    //TODO 15.01.2024: добавить тест на метод disableTfa
}