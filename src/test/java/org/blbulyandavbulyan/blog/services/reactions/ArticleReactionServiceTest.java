package org.blbulyandavbulyan.blog.services.reactions;

import org.blbulyandavbulyan.blog.entities.Article;
import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.entities.reactions.ArticleReaction;
import org.blbulyandavbulyan.blog.repositories.reactions.ArticleReactionRepository;
import org.blbulyandavbulyan.blog.repositories.reactions.IReactionRepository;
import org.blbulyandavbulyan.blog.services.ArticleService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleReactionServiceTest extends AbstractReactionServiceTest<ArticleReaction, Article, ArticleReactionRepository> {
    @Mock
    private ArticleService articleService;
    @Mock
    private ArticleReactionRepository articleReactionRepository;
    @InjectMocks
    private ArticleReactionService underTest;
    @Override
    protected AbstractReactionService<ArticleReaction, Article, ArticleReactionRepository> getUnderTest() {
        return underTest;
    }
    @Override
    protected ArticleReaction createReactionMock() {
        return mock(ArticleReaction.class);
    }

    @Override
    protected ArticleReactionRepository getReactionRepositoryMock() {
        return articleReactionRepository;
    }

    @Override
    void reactWhenReactionDoesNotExistTemplate(boolean liked) {
        Article target = new Article();
        User liker = new User();
        long targetId = 1L;
        String likerName = "testuser";
        when(articleService.getReferenceById(targetId)).thenReturn(target);
        when(userService.getReferenceByName(likerName)).thenReturn(liker);
        when(articleReactionRepository.findByTargetIdAndLikerName(targetId, likerName)).thenReturn(Optional.empty());
        assertDoesNotThrow(()->underTest.react(targetId, likerName, liked));
        var argumentCaptor = ArgumentCaptor.forClass(ArticleReaction.class);
        verify((IReactionRepository<ArticleReaction>)articleReactionRepository, times(1)).save(argumentCaptor.capture());
        ArticleReaction actualReaction = argumentCaptor.getValue();
        assertSame(target, actualReaction.getTarget());
        assertSame(liker, actualReaction.getLiker());
        assertEquals(liked, actualReaction.isLiked());
    }
}