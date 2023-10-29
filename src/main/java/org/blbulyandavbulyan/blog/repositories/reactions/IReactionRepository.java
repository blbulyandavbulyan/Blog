package org.blbulyandavbulyan.blog.repositories.reactions;

import org.blbulyandavbulyan.blog.dtos.reactions.ReactionStatistics;
import org.blbulyandavbulyan.blog.entities.reactions.IReaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Этот интерфейс предоставляет абстракцию для репозитория реакций
 * @param <RT> тип реакции, предполагается что это JPA Entity, должен имплементировать {@link IReaction}
 */
public interface IReactionRepository<RT extends IReaction> {
    Optional<RT> findByTargetIdAndLikerName(Long targetId, String likerName);
    @Transactional
    void deleteByTargetIdAndLikerName(Long targetId, String likerName);
    @Query("""
        SELECT
            new org.blbulyandavbulyan.blog.dtos.reactions.ReactionStatistics(
                COALESCE(SUM(CASE WHEN e.liked = true THEN 1 ELSE 0 END), 0),
                COALESCE(SUM(CASE WHEN e.liked = false THEN 1 ELSE 0 END), 0)
            )
        FROM
            #{#entityName} e
        WHERE
        e.target.id = :targetId
    """)
    ReactionStatistics getStatistics(@Param("targetId") Long targetId);
    RT save(RT reaction);
}
