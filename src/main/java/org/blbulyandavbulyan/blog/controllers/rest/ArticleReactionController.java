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
@RequestMapping("/api/v1/reactions/article")
@RequiredArgsConstructor
@Validated
@Tags({@Tag(name = "reaction"), @Tag(name = "article")})
public class ArticleReactionController {
    private final ArticleReactionService articleReactionService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrAlterReaction(@RequestBody @Validated ArticleReactionDTO articleReactionDTO, Principal principal){
        //TODO 31.03.2024: Снова нелогичный путь, лучше было бы выбрать /api/v1/articles/{articleId}/reaction
        // именно в единственном числе, поскольку мы добавляем свою реакцию, а не меняем чужие
        articleReactionService.react(articleReactionDTO.articleId(), principal.getName(), articleReactionDTO.liked());
    }
    @DeleteMapping("/{articleId}")
    public void removeReaction(@PathVariable @ValidArticleId Long articleId, Principal principal){
        //TODO 31.03.2024: Снова нелогичный путь, лучше было бы выбрать /api/v1/articles/{articleId}/reaction
        // именно в единственном числе, поскольку мы удаляем только свою реакцию
        articleReactionService.removeReaction(articleId, principal.getName());
    }
    @GetMapping("/statistics/{articleId}")
    public ReactionStatistics getReactionStatistics(@PathVariable @ValidArticleId Long articleId){
        //TODO 31.03.2024: Возможно стоит переделать этот путь в /api/v1/articles/{articleId}/reaction-statistics
        // так же подумать над тем а нужен ли реально отдельный endpoint для статистики или их можно склеить вместе с findReaction
        return articleReactionService.getStatistics(articleId);
    }
    @GetMapping("/{articleId}")
    public ReactionResponse findReaction(@PathVariable @ValidArticleId Long articleId, Principal principal){
        return articleReactionService.getReaction(articleId, principal.getName()).
                map(r->new ReactionResponse(r.isLiked(), true))
                .orElseGet(()->new ReactionResponse(false, false));
    }
}
