package org.blbulyandavbulyan.blog.controllers;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.services.ArticlesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleTemplatesController {
    private final ArticlesService articlesService;
    @GetMapping("/{id}")
    public String showArticleById(@PathVariable Long id, Model model){
        model.addAttribute("article", articlesService.getById(id));
        return "article";
    }
    @GetMapping
    public String showArticles(){
        return "articles";
    }
}
