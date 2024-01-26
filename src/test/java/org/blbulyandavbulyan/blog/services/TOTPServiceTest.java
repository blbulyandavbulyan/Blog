package org.blbulyandavbulyan.blog.services;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.util.Utils;
import org.blbulyandavbulyan.blog.exceptions.security.tfa.TOTPQrCodeGenerationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TOTPServiceTest {
    @Mock
    private CodeVerifier codeVerifier;
    @Mock
    private SecretGenerator secretGenerator;
    @Mock
    private QrGenerator qrGenerator;
    @Mock
    private QrData.Builder qrDataBuilder;
    @InjectMocks
    private TOTPService underTest;

    @Test
    void generateSecretNewSecretTest() {
        String expectedSecret = "newgeneratedsecret";
        when(secretGenerator.generate()).thenReturn(expectedSecret);
        String actualSecret = assertDoesNotThrow(() -> underTest.generateNewSecret());
        assertSame(expectedSecret, actualSecret);
        verifyNoInteractions(qrGenerator, qrDataBuilder, codeVerifier);
    }

    @Test
    void generateQrCodeImageUri() throws QrGenerationException {
        byte[] expectedImageData = new byte[50];
        new Random().nextBytes(expectedImageData);
        byte[] imageDataCopy = Arrays.copyOf(expectedImageData, expectedImageData.length);
        final String username = "testusername";
        final String secret = "testsecretverylongsecret";
        final String imageMimeType = "uniqueimagemimetype";
        when(qrDataBuilder.secret(secret)).thenReturn(qrDataBuilder);
        when(qrDataBuilder.label(username)).thenReturn(qrDataBuilder);
        QrData qrData = mock(QrData.class);
        when(qrDataBuilder.build()).thenReturn(qrData);
        when(qrGenerator.generate(qrData)).thenReturn(imageDataCopy);
        when(qrGenerator.getImageMimeType()).thenReturn(imageMimeType);
        try (MockedStatic<Utils> totpUtils = mockStatic(Utils.class)) {
            String expectedQrCodeString = "qrcodestring";
            totpUtils.when(() -> Utils.getDataUriForImage(imageDataCopy, imageMimeType)).thenReturn(expectedQrCodeString);
            String actualQrCodeString = assertDoesNotThrow(() -> underTest.generateQrCodeImageUri(secret, username));
            assertSame(expectedQrCodeString, actualQrCodeString);
            ArgumentCaptor<byte[]> imageDataArgumentCaptor = ArgumentCaptor.forClass(byte[].class);
            totpUtils.verify(() -> Utils.getDataUriForImage(imageDataArgumentCaptor.capture(), same(imageMimeType)));
            byte[] providedImageData = imageDataArgumentCaptor.getValue();
            assertArrayEquals(expectedImageData, providedImageData, "Image data was modified!");
        }
        verifyNoInteractions(secretGenerator, codeVerifier);
        verifyNoMoreInteractions(qrDataBuilder, qrGenerator);
    }

    @Test
    void generateQrCodeImageUriWhenQrGeneratorThrewAnException() throws QrGenerationException {
        final String username = "testusername";
        final String secret = "testsecretverylongsecret";
        when(qrDataBuilder.secret(secret)).thenReturn(qrDataBuilder);
        when(qrDataBuilder.label(username)).thenReturn(qrDataBuilder);
        QrData qrData = mock(QrData.class);
        when(qrDataBuilder.build()).thenReturn(qrData);
        when(qrGenerator.generate(qrData)).thenThrow(QrGenerationException.class);
        assertThrows(TOTPQrCodeGenerationException.class, () -> underTest.generateQrCodeImageUri(secret, username));
        verifyNoInteractions(secretGenerator, codeVerifier);
        verifyNoMoreInteractions(qrDataBuilder, qrGenerator);
    }

    @ParameterizedTest(name = "is valid code = {0}")
    @ValueSource(booleans = {true, false})
    void isNotValidCodeTest(boolean isValidCode) {
        final String secret = "testsecretverylongsecret";
        final String code = "testcode";
        when(codeVerifier.isValidCode(secret, code)).thenReturn(isValidCode);
        boolean actual = assertDoesNotThrow(() -> underTest.isNotValidCode(secret, code));
        assertNotEquals(isValidCode, actual);
    }
}