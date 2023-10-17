package org.blbulyandavbulyan.blog.services;

import org.blbulyandavbulyan.blog.dtos.comment.CommentResponse;
import org.blbulyandavbulyan.blog.entities.Article;
import org.blbulyandavbulyan.blog.entities.Comment;
import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.exceptions.articles.ArticleNotFoundException;
import org.blbulyandavbulyan.blog.exceptions.comments.CommentNotFoundException;
import org.blbulyandavbulyan.blog.exceptions.security.AccessDeniedException;
import org.blbulyandavbulyan.blog.repositories.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserService userService;
    @Mock
    private ArticleService articleService;
    @Mock
    private SecurityService securityService;
    @InjectMocks
    private CommentService underTest;

    @Test
    void publishComment() {
        String publisherName = "testpublisher";
        long articleId = 1L;
        long expectedCommentId = 222L;
        String text = "test text";
        User publisher = new User();
        Article article = new Article();
        when(commentRepository.save(any(Comment.class))).then(invocation -> {
            Comment argument = invocation.getArgument(0, Comment.class);
            argument.setCommentId(expectedCommentId);
            return argument;
        });
        when(userService.getReferenceByName(publisherName)).thenReturn(publisher);
        when(articleService.getReferenceById(articleId)).thenReturn(article);
        assertDoesNotThrow(()-> underTest.publishComment(publisherName, articleId, text));
        ArgumentCaptor<Comment> commentArgumentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository, only()).save(commentArgumentCaptor.capture());
        Comment savedComment = commentArgumentCaptor.getValue();
        assertEquals(expectedCommentId, savedComment.getCommentId());
        assertEquals(text, savedComment.getText());
        assertEquals(publisher, savedComment.getAuthor());
        assertEquals(article, savedComment.getArticle());
    }

    @Test
    @DisplayName("deleteComment when it exists")
    void deleteCommentWhenAuthorFound() {
        long commentId = 1L;
        String authorName = "testauthor";
        Authentication authentication = mock(Authentication.class);
        when(commentRepository.findCommentAuthorNameByCommentId(commentId)).thenReturn(Optional.of(authorName));
        doAnswer(invocation -> {
            invocation.getArgument(2, Runnable.class).run();
            return null;
        }).when(securityService).executeIfExecutorIsAdminOrEqualToTarget(same(authentication), eq(authorName), any(Runnable.class));
        assertDoesNotThrow(()-> underTest.deleteComment(commentId, authentication));
        verify(commentRepository, times(1)).deleteById(commentId);
    }
    @Test
    @DisplayName("deleteComment when it doesn't exist")
    void deleteCommentWhenDoesNotExist(){
        long commentId = 1L;
        when(commentRepository.findCommentAuthorNameByCommentId(commentId)).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, ()-> underTest.deleteComment(commentId, mock(Authentication.class)));
        verify(securityService, never()).executeIfExecutorIsAdminOrEqualToTarget(any(), any(), any());
        verify(commentRepository, never()).deleteById(commentId);
    }

    @Test
    @DisplayName("editComment when it exists and author equal to executor")
    void normalEditComment() {
        long commentId = 1L;
        String authorName = "testauthor";
        String text = "test text";
        when(commentRepository.findCommentAuthorNameByCommentId(commentId)).thenReturn(Optional.of(authorName));
        assertDoesNotThrow(()-> underTest.editComment(commentId, text, authorName));
        verify(commentRepository, times(1)).updateTextByCommentId(commentId, text);
    }

    @Test
    @DisplayName("editComment when it doesn't exist")
    void editCommentWhenItDoesNotExist() {
        long commentId = 1L;
        when(commentRepository.findCommentAuthorNameByCommentId(commentId)).thenReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, ()-> underTest.editComment(commentId, "test", "test"));
    }

    @Test
    @DisplayName("editComment when author doesn't equal to executor")
    void editCommentWhenAuthorDoesNotEqualToExecutor() {
        long commentId = 1L;
        String authorName = "testauthor";
        String text = "test text";
        when(commentRepository.findCommentAuthorNameByCommentId(commentId)).thenReturn(Optional.of(authorName));
        assertThrows(AccessDeniedException.class, ()-> underTest.editComment(commentId, text, "test"));
        verify(commentRepository, never()).updateTextByCommentId(commentId, text);
    }

    @Test
    @DisplayName("getCommentDTOsForArticleId when article exists")
    void getCommentDTOsForArticleIdWhenArticleExists() {
        long articleId = 1L;
        int pageNumber = 0;
        int pageSize = 10;
        Page<CommentResponse> expectedPage = mock(Page.class);
        when(articleService.existsById(articleId)).thenReturn(true);
        when(commentRepository.findAllByArticleId(eq(articleId), eq(PageRequest.of(pageNumber, pageSize)), eq(CommentResponse.class))).thenReturn(expectedPage);
        Page<CommentResponse> actualPage = assertDoesNotThrow(() -> underTest.getCommentDTOsForArticleId(articleId, pageNumber, pageSize));
        assertSame(expectedPage, actualPage);
    }

    @Test
    @DisplayName("getCommentDTOsForArticleId when article doesn't exist")
    void getCommentDTOsForNotExistingArticle() {
        long articleId = 1L;
        when(articleService.existsById(articleId)).thenReturn(false);
        assertThrows(ArticleNotFoundException.class, ()->underTest.getCommentDTOsForArticleId(articleId, 0, 10));
        verifyNoInteractions(commentRepository);
    }
}