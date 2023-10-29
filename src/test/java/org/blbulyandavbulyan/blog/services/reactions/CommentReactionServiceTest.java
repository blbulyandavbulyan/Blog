package org.blbulyandavbulyan.blog.services.reactions;

import org.blbulyandavbulyan.blog.entities.Comment;
import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.entities.reactions.CommentReaction;
import org.blbulyandavbulyan.blog.repositories.reactions.CommentReactionRepository;
import org.blbulyandavbulyan.blog.repositories.reactions.IReactionRepository;
import org.blbulyandavbulyan.blog.services.CommentService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class CommentReactionServiceTest extends AbstractReactionServiceTest<CommentReaction, Comment, CommentReactionRepository>{
    @Mock
    private CommentService commentService;
    @Mock
    private CommentReactionRepository commentReactionRepository;
    @InjectMocks
    private CommentReactionService underTest;
    @Override
    protected AbstractReactionService<CommentReaction, Comment, CommentReactionRepository> getUnderTest() {
        return underTest;
    }

    @Override
    protected CommentReaction createReactionMock() {
        return mock(CommentReaction.class);
    }

    @Override
    protected CommentReactionRepository getReactionRepositoryMock() {
        return commentReactionRepository;
    }

    @Override
    void reactWhenReactionDoesNotExistTemplate(boolean liked) {
        Comment target = new Comment();
        User liker = new User();
        long targetId = 1L;
        String likerName = "testuser";
        when(commentService.getReferenceById(targetId)).thenReturn(target);
        when(userService.getReferenceByName(likerName)).thenReturn(liker);
        when(commentReactionRepository.findByTargetIdAndLikerName(targetId, likerName)).thenReturn(Optional.empty());
        assertDoesNotThrow(()->underTest.react(targetId, likerName, liked));
        var argumentCaptor = ArgumentCaptor.forClass(CommentReaction.class);
        verify((IReactionRepository<CommentReaction>) commentReactionRepository, times(1)).save(argumentCaptor.capture());
        CommentReaction actualReaction = argumentCaptor.getValue();
        assertSame(target, actualReaction.getTarget());
        assertSame(liker, actualReaction.getLiker());
        assertEquals(liked, actualReaction.isLiked());
    }
}