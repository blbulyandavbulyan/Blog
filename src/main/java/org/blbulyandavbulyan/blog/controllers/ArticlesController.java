package org.blbulyandavbulyan.blog.controllers;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.ArticleForPublishing;
import org.blbulyandavbulyan.blog.exceptions.AppError;
import org.blbulyandavbulyan.blog.exceptions.ArticleNotFoundException;
import org.blbulyandavbulyan.blog.exceptions.UserNotFoundException;
import org.blbulyandavbulyan.blog.services.ArticlesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticlesController {
    private final ArticlesService articlesService;
    @GetMapping("/get/{id}")
    @ResponseBody
    public ResponseEntity<?> getById(@PathVariable Long id){
        try {
            return ResponseEntity.ok(articlesService.getById(id));
        }
        catch (ArticleNotFoundException e){
            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/publish")
    @ResponseBody
    public ResponseEntity<?> publishArticle(@RequestBody ArticleForPublishing articleForPublishing, Principal principal){
        try {
            articlesService.publishArticle(articleForPublishing, principal.getName());
            return ResponseEntity.ok("article was published!");
        }
        catch (UserNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
