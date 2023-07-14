package org.blbulyandavbulyan.blog.services;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.ArticleDto;
import org.blbulyandavbulyan.blog.dtos.ArticleForPublishing;
import org.blbulyandavbulyan.blog.dtos.ArticlePublished;
import org.blbulyandavbulyan.blog.entities.Article;
import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.exceptions.ArticleNotFoundException;
import org.blbulyandavbulyan.blog.repositories.ArticleRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticlesService {
    private final ArticleRepository articleRepository;
    private final UserService userService;
    public ArticlePublished publishArticle(String title, String text, User publisher){
        Article article = new Article();
        article.setTitle(title);
        article.setText(text);
        article.setPublisher(publisher);
        articleRepository.save(article);
        return new ArticlePublished(article.getArticleId());
    }

    public ArticleDto getById(Long id) {
        return articleRepository.findArticleDtoById(id).orElseThrow(()->new ArticleNotFoundException("Article with id " + id + " not found"));
    }

    public ArticlePublished publishArticle(ArticleForPublishing articleForPublishing, String publisherName) {
        User user = userService.findByName(publisherName);
        return publishArticle(articleForPublishing.title(), articleForPublishing.text(), user);
    }
}
