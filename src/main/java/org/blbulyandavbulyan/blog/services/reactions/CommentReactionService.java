package org.blbulyandavbulyan.blog.services.reactions;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.entities.Comment;
import org.blbulyandavbulyan.blog.entities.CommentReaction;
import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.repositories.CommentReactionRepository;
import org.blbulyandavbulyan.blog.services.CommentService;
import org.blbulyandavbulyan.blog.services.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentReactionService {
    private final UserService userService;
    private final CommentService commentService;
    private final CommentReactionRepository commentReactionRepository;
    /**
     * Данный метод удаляет реакцию от данного пользователя к данной статье
     * @param id id статьи, с которой нужно убрать реакцию
     * @param username имя пользователя, от которого нужно убрать реакцию
     */
    public void removeReaction(Long id, String username){
        commentReactionRepository.deleteByCommentAndLiker(commentService.getReferenceById(id), userService.getReferenceByName(username));
    }
    public void react(Long id, String username, boolean liked){
        Comment comment = commentService.getReferenceById(id);
        User liker = userService.getReferenceByName(username);
        CommentReaction reaction = commentReactionRepository.findByCommentAndLiker(comment, liker)
                .orElseGet(() -> new CommentReaction(comment, liker));
        reaction.setLiked(liked);
        commentReactionRepository.save(reaction);
    }
}
