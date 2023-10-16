package org.blbulyandavbulyan.blog.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.annotations.validation.comment.ValidCommentId;
import org.blbulyandavbulyan.blog.dtos.reactions.CommentReactionDTO;
import org.blbulyandavbulyan.blog.services.reactions.CommentReactionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reactions/comment")
public class CommentReactionController {
    private final CommentReactionService commentReactionService;

    @DeleteMapping("/{commentId}")
    public void removeReaction(@PathVariable @ValidCommentId Long commentId, Principal principal) {
        commentReactionService.removeReaction(commentId, principal.getName());
    }
    @PostMapping
    public void createOrAlterReaction(@Validated @RequestBody CommentReactionDTO commentReactionDTO, Principal principal){
        commentReactionService.react(commentReactionDTO.commentId(), principal.getName(), commentReactionDTO.liked());
    }
}
