package org.blbulyandavbulyan.blog.controllers.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.annotations.validation.comment.ValidCommentId;
import org.blbulyandavbulyan.blog.dtos.reactions.CommentReactionDTO;
import org.blbulyandavbulyan.blog.dtos.reactions.ReactionResponse;
import org.blbulyandavbulyan.blog.dtos.reactions.ReactionStatistics;
import org.blbulyandavbulyan.blog.services.reactions.CommentReactionService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reactions/comment")
@Tags({@Tag(name = "reaction"), @Tag(name = "comment")})
public class CommentReactionController {
    private final CommentReactionService commentReactionService;

    @DeleteMapping("/{commentId}")
    public void removeReaction(@PathVariable @ValidCommentId Long commentId, Principal principal) {
        //TODO 31.03.2024: не интуитивный путь к реакциям для комментов, было бы более логичным что-то типа /api/v1/comments/{commentId}/reactions
        commentReactionService.removeReaction(commentId, principal.getName());
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrAlterReaction(@Validated @RequestBody CommentReactionDTO commentReactionDTO, Principal principal){
        commentReactionService.react(commentReactionDTO.commentId(), principal.getName(), commentReactionDTO.liked());
    }
    @GetMapping("/statistics/{commentId}")
    public ReactionStatistics getReactionStatistics(@PathVariable @ValidCommentId Long commentId){
        //TODO 31.03.2024: Не интуитивный путь, возможно было бы логичнее /api/v1/comments/{commentId}/reaction-statistics
        // Не понятно вообще зачем он выделен отдельно, возможно из-за того что для другого который получает реакцию пользователя нужно обязательно чтобы пользователь был авторизован
        // возможно так же стоит рассмотреть вариант объединения этих endpoint в один с логикой внутри которая подкладывает дополнительное поле с реакцией самого пользователя(если он авторизован)
        return commentReactionService.getStatistics(commentId);
    }
    @GetMapping("/{commentId}")
    public ReactionResponse findReaction(@PathVariable @ValidCommentId Long commentId, Principal principal){
        //TODO 31.03.2024: не интуитивный путь к реакциям для комментов, было бы более логичным что-то типа /api/v1/comments/{commentId}/reactions
        return commentReactionService.getReaction(commentId, principal.getName()).
                map(r->new ReactionResponse(r.isLiked(), true))
                .orElseGet(()->new ReactionResponse(false, false));
    }
}
