package org.blbulyandavbulyan.blog.repositories.reactions;

import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.entities.reactions.IReaction;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Этот интерфейс предоставляет абстракцию для репозитория реакций
 * @param <RT> тип реакции, предполагается что это JPA Entity, должен имплементировать {@link IReaction}
 * @param <TT> тип цели, для которой предназначена реакция, поле этого типа должно быть в классе типа RT и оно должно называться target
 */
public interface IReactionRepository<RT extends IReaction, TT> {
    Optional<RT> findByTargetAndLiker(TT target, User liker);
    @Modifying
    @Transactional
    void deleteByTargetAndLiker(TT target, User liker);
    RT save(RT reaction);
}
