package org.blbulyandavbulyan.blog.repositories.reactions;

import org.blbulyandavbulyan.blog.entities.IReaction;
import org.blbulyandavbulyan.blog.entities.User;

import java.util.Optional;

/**
 * Этот интерфейс предоставляет абстракцию для репозитория реакций
 * @param <RT> тип реакции, предполагается что это JPA Entity, должен имплементировать {@link IReaction}
 * @param <TT> тип цели, для которой предназначена реакция, поле этого типа должно быть в классе типа RT и оно должно называться target
 */
public interface IReactionRepository<RT extends IReaction, TT> {
    Optional<RT> findByTargetAndLiker(TT target, User liker);
    void deleteByTargetAndLiker(TT target, User liker);
    RT save(RT reaction);
}
