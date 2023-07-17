package org.blbulyandavbulyan.blog.controllers;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.article.ArticleDto;
import org.blbulyandavbulyan.blog.dtos.article.ArticleForPublishing;
import org.blbulyandavbulyan.blog.dtos.article.ArticleInfoDTO;
import org.blbulyandavbulyan.blog.dtos.article.ArticlePublished;
import org.blbulyandavbulyan.blog.services.ArticlesService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/articles")
public class ArticlesController {
    private final ArticlesService articlesService;

    @GetMapping("/{id}")
    public ArticleDto getById(@PathVariable Long id) {
        return articlesService.getById(id);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteArticleById(@PathVariable Long id) {
        articlesService.deleteById(id);
    }

    @Secured("ROLE_PUBLISHER")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArticlePublished publishArticle(@RequestBody ArticleForPublishing articleForPublishing, Principal principal) {
        return articlesService.publishArticle(articleForPublishing, principal.getName());
    }

    @GetMapping("/info/all")
    public Page<ArticleInfoDTO> getInfoAboutAllArticles(@RequestParam(defaultValue = "5") Integer pageSize, @RequestParam(defaultValue = "0") Integer pageNumber) {
        return articlesService.getInfoAboutAll(pageSize, pageNumber);
    }
}
