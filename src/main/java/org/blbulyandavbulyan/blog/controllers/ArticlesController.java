package org.blbulyandavbulyan.blog.controllers;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.article.ArticleForPublishing;
import org.blbulyandavbulyan.blog.dtos.article.ArticleInfoDTO;
import org.blbulyandavbulyan.blog.dtos.article.ArticlePublished;
import org.blbulyandavbulyan.blog.exceptions.AppError;
import org.blbulyandavbulyan.blog.exceptions.ArticleNotFoundException;
import org.blbulyandavbulyan.blog.exceptions.UserNotFoundException;
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
    public ResponseEntity<?> getById(@PathVariable Long id){
        try {
            return ResponseEntity.ok(articlesService.getById(id));
        }
        catch (ArticleNotFoundException e){
            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticleById(@PathVariable Long id){
        boolean deleted = articlesService.deleteById(id);
        return (deleted ? ResponseEntity.ok() : ResponseEntity.notFound()).build();
    }
    @Secured("ROLE_PUBLISHER")
    @PostMapping
    public ResponseEntity<?> publishArticle(@RequestBody ArticleForPublishing articleForPublishing, Principal principal){
        try {
            ArticlePublished articlePublished = articlesService.publishArticle(articleForPublishing, principal.getName());
            return new ResponseEntity<>(articlePublished, HttpStatus.CREATED);
        }
        catch (UserNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/info/all")
    public Page<ArticleInfoDTO> getInfoAboutAllArticles(@RequestParam(defaultValue = "5") Integer pageSize, @RequestParam(defaultValue = "0") Integer pageNumber){
        return articlesService.getInfoAboutAll(pageSize, pageNumber);
    }
}
