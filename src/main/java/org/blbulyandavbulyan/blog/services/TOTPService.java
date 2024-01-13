package org.blbulyandavbulyan.blog.services;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.blbulyandavbulyan.blog.exceptions.TOTPQrCodeGenerationException;
import org.springframework.stereotype.Service;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

@Service
@RequiredArgsConstructor
@Slf4j
public class TOTPService {
    private final CodeVerifier codeVerifier;
    private final SecretGenerator secretGenerator;
    private final QrGenerator qrGenerator;
    private final QrData.Builder qrDataBuilder;
    public String generateNewSecret() {
        return secretGenerator.generate();
    }

    public String generateQrCodeImageUri(String secret) {
        QrData data = qrDataBuilder.secret(secret).build();
        byte[] imageData;
        try {
            imageData = qrGenerator.generate(data);
        } catch (QrGenerationException e) {
            throw new TOTPQrCodeGenerationException(e);
        }
        return getDataUriForImage(imageData, qrGenerator.getImageMimeType());
    }

    public boolean verifyCode(String secret, String code) {
        return codeVerifier.isValidCode(secret, code);
    }
}
