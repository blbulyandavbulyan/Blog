package org.blbulyandavbulyan.blog.services.reactions;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.reactions.ReactionStatistics;
import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.entities.reactions.IReaction;
import org.blbulyandavbulyan.blog.repositories.reactions.IReactionRepository;
import org.blbulyandavbulyan.blog.services.UserService;

import java.util.function.Function;

/**
 * Предоставляет абстрактный сервис реакций
 * @param <RT> тип реакции, такой же должен быть и в {@link IReactionRepository}
 * @param <TT> тип цели, аналогично типу цели в {@link IReactionRepository}
 * @param <R> тип репозитория реакций, должен быть наследником от {@link IReactionRepository}
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AbstractReactionService <RT extends IReaction, TT, R extends IReactionRepository<RT, TT>>{
    /**
     * Предоставляет фабрику реакций
     * @param <RT> тип реакций, производимый фабрикой, должен быть наследником {@link IReaction}
     * @param <TT> тип цели, к которой относится реакция
     */
    @FunctionalInterface
    protected interface ReactionFactory<RT extends IReaction, TT>{
        /**
         * Создаёт реакцию
         * @param target цель, к которой будет относиться реакция
         * @param liker отреагировавший пользователь
         * @return созданная реакция с заданным пользователем и целью
         */
        RT create(TT target, User liker);
    }
    private final UserService userService;
    private final R repository;
    private final ReactionFactory<RT, TT> reactionFactory;
    private final Function<Long, TT> getTargetReference;
    private TT getReferenceById(Long id){
        return getTargetReference.apply(id);
    }

    /**
     * Данный метод удаляет реакцию по id цели и имени реагировавшего
     * @param targetId ИД цели
     * @param actorUsername имя реагировавшего пользователя
     */
    public void removeReaction(Long targetId, String actorUsername){
        repository.deleteByTargetAndLiker(getReferenceById(targetId), userService.getReferenceByName(actorUsername));
    }

    /**
     * Метод реагирует на заданную цель от имени заданного пользователя
     * @param targetId ИД цели, на которую будет реакция
     * @param actorUsername имя пользователя, от которого будет реакция
     * @param liked true если понравилось, иначе false
     */
    public void react(Long targetId, String actorUsername, boolean liked){
        var target =  getReferenceById(targetId);
        User liker = userService.getReferenceByName(actorUsername);
        RT reaction = repository.findByTargetAndLiker(target, liker)
                .orElseGet(() -> reactionFactory.create(target, liker));
        reaction.setLiked(liked);
        repository.save(reaction);
    }

    /**
     * Метод возвращает статистику по реакциям для заданной цели
     * @param targetId ИД цели
     * @return искомая статистика
     */
    public ReactionStatistics getStatistics(Long targetId){
        return repository.getStatistics(getReferenceById(targetId));
    }
}
