package org.blbulyandavbulyan.blog.controllers.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.annotations.validation.article.ValidArticleId;
import org.blbulyandavbulyan.blog.dtos.reactions.ArticleReactionDTO;
import org.blbulyandavbulyan.blog.dtos.reactions.ReactionResponse;
import org.blbulyandavbulyan.blog.dtos.reactions.ReactionStatistics;
import org.blbulyandavbulyan.blog.services.reactions.ArticleReactionService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/articles/{articleId}/reactions")
@RequiredArgsConstructor
@Validated
@Tags({@Tag(name = "reaction"), @Tag(name = "article")})
public class ArticleReactionController {
    private final ArticleReactionService articleReactionService;
    @PostMapping("/my")
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrAlterReaction(@PathVariable @ValidArticleId Long articleId, @RequestBody @Validated ArticleReactionDTO articleReactionDTO, Principal principal){
        articleReactionService.react(articleId, principal.getName(), articleReactionDTO.liked());
    }
    @DeleteMapping("/my")
    public void removeReaction(@PathVariable @ValidArticleId Long articleId, Principal principal){
        articleReactionService.removeReaction(articleId, principal.getName());
    }
    @GetMapping("/statistics")
    public ReactionStatistics getReactionStatistics(@PathVariable @ValidArticleId Long articleId){
        return articleReactionService.getStatistics(articleId);
    }
    @GetMapping("/my")
    public ReactionResponse findReaction(@PathVariable @ValidArticleId Long articleId, Principal principal){
        return articleReactionService.getReaction(articleId, principal.getName()).
                map(r->new ReactionResponse(r.isLiked(), true))
                .orElseGet(()->new ReactionResponse(false, false));
    }
}
