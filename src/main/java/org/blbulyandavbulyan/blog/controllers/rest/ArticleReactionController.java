package org.blbulyandavbulyan.blog.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.annotations.validation.article.ValidArticleId;
import org.blbulyandavbulyan.blog.dtos.reactions.ArticleReactionDTO;
import org.blbulyandavbulyan.blog.dtos.reactions.ReactionStatistics;
import org.blbulyandavbulyan.blog.services.reactions.ArticleReactionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/reactions/article")
@RequiredArgsConstructor
@Validated
public class ArticleReactionController {
    private final ArticleReactionService articleReactionService;
    @PostMapping
    public void createOrAlterReaction(@RequestBody @Validated ArticleReactionDTO articleReactionDTO, Principal principal){
        articleReactionService.react(articleReactionDTO.articleId(), principal.getName(), articleReactionDTO.liked());
    }
    @DeleteMapping("/{articleId}")
    public void removeReaction(@PathVariable @ValidArticleId Long articleId, Principal principal){
        articleReactionService.removeReaction(articleId, principal.getName());
    }
    @GetMapping("/{articleId}")
    public ReactionStatistics getReactionStatistics(@PathVariable @ValidArticleId Long articleId){
        return articleReactionService.getStatistics(articleId);
    }
}
