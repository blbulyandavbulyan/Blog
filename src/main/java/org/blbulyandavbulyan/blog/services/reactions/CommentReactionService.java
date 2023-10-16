package org.blbulyandavbulyan.blog.services.reactions;

import org.blbulyandavbulyan.blog.entities.Comment;
import org.blbulyandavbulyan.blog.entities.reactions.CommentReaction;
import org.blbulyandavbulyan.blog.repositories.reactions.CommentReactionRepository;
import org.blbulyandavbulyan.blog.services.CommentService;
import org.blbulyandavbulyan.blog.services.UserService;
import org.springframework.stereotype.Service;

@Service
public class CommentReactionService extends AbstractReactionService<CommentReaction, Comment, CommentReactionRepository>{
    public CommentReactionService(UserService userService, CommentService commentService, CommentReactionRepository repository) {
        super(userService, repository, CommentReaction::new, commentService::getReferenceById);
    }
}
