package org.blbulyandavbulyan.blog.dtos.authorization;

import jakarta.validation.constraints.NotBlank;

/**
 * Запрос верификации в случае если включена TFA (двухфакторная аутентификация)
 * @param jwtToken jwt токен, полученный в результате успешного первого этапа
 * @param code код подтверждения
 */
public record VerificationRequest(@NotBlank String jwtToken, @NotBlank String code) {
}
