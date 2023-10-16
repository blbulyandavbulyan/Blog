package org.blbulyandavbulyan.blog.entities;

/**
 * Предоставляет интерфейс для реакций
 */
public interface IReaction {
    /**
     * Изменяет реакцию
     * @param liked true если понравилось, иначе false
     */
    void setLiked(boolean liked);
}
