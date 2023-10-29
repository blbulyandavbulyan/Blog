package org.blbulyandavbulyan.blog.dtos.reactions;

/**
 * Данная запись предоставляет статистику по реакциям для заданной цели
 * @param likesCount количество лайков
 * @param dislikesCount количество дизлайков
 */
public record ReactionStatistics(long likesCount, long dislikesCount) {
}
