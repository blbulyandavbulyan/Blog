package org.blbulyandavbulyan.blog.dtos.authorization;

/**
 * Данная запись отправляется в виде JSON в случае успешной JWT авторизации
 * @param token JWT токен, который будет использоваться для идентификации пользователя
 * @param tfaRequired показывает, требуется ли двухфакторная аутентификация
 */
public record AuthenticationResponse(String token, boolean tfaRequired) {
}
