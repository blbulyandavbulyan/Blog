package org.blbulyandavbulyan.blog.dtos.authorization;

/**
 * Содержит информацию о настройках TFA для пользователя
 * @param enabled
 */
public record TFAStatus(boolean enabled) {
}
