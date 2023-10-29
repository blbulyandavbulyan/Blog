package org.blbulyandavbulyan.blog.services.reactions;

import org.blbulyandavbulyan.blog.dtos.reactions.ReactionStatistics;
import org.blbulyandavbulyan.blog.entities.reactions.IReaction;
import org.blbulyandavbulyan.blog.repositories.reactions.IReactionRepository;
import org.blbulyandavbulyan.blog.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

abstract class  AbstractReactionServiceTest<RT extends IReaction, TT, RR extends IReactionRepository<RT>> {
    @Mock
    protected UserService userService;
    protected abstract AbstractReactionService<RT, TT, RR> getUnderTest();
    protected abstract RT createReactionMock();
    protected abstract RR getReactionRepositoryMock();
    @Test
    void removeReaction() {
        Long targetId = 1L;
        String likerName = "testuser123";
        getUnderTest().removeReaction(targetId, likerName);
        verify( getReactionRepositoryMock(), only()).deleteByTargetIdAndLikerName(targetId, likerName);
    }


    @Test
    void getStatistics() {
        long targetId = 1L;
        ReactionStatistics expected = new ReactionStatistics(1, 2);
        when( getReactionRepositoryMock().getStatistics(targetId)).thenReturn(expected);
        ReactionStatistics actual = getUnderTest().getStatistics(targetId);
        assertThat(actual).isEqualTo(expected);
        verify( getReactionRepositoryMock(), only()).getStatistics(targetId);
    }
    void reactWhenReactionExistTemplate(boolean liked){
        long targetId = 1L;
        String likerName = "testuser";
        RT reaction = createReactionMock();
        when( getReactionRepositoryMock().findByTargetIdAndLikerName(targetId, likerName)).thenReturn(Optional.ofNullable(reaction));
        assertDoesNotThrow(()->getUnderTest().react(targetId, likerName, liked));
        verify(getReactionRepositoryMock(), times(1)).findByTargetIdAndLikerName(targetId, likerName);
        verify(reaction, only()).setLiked(liked);
        verify( getReactionRepositoryMock(), times(1)).save(reaction);
    }
    @Test
    void reactWhenReactionExistsAndLikerLiked(){
        reactWhenReactionExistTemplate(true);
    }

    @Test
    void reactWhenReactionExistsAndLikerDisliked() {
        reactWhenReactionExistTemplate(false);
    }
    abstract void reactWhenReactionDoesNotExistTemplate(boolean liked);

    @Test
    void reactWhenReactionDoesNotExistAndLikerLiked() {
        reactWhenReactionDoesNotExistTemplate(true);
    }

    @Test
    void reactWhenReactionDoesNotExistAndLikedDisliked() {
        reactWhenReactionDoesNotExistTemplate(false);
    }
}