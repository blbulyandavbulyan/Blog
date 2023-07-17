package org.blbulyandavbulyan.blog.services;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.article.ArticleDto;
import org.blbulyandavbulyan.blog.dtos.article.ArticleForPublishing;
import org.blbulyandavbulyan.blog.dtos.article.ArticleInfoDTO;
import org.blbulyandavbulyan.blog.dtos.article.ArticlePublished;
import org.blbulyandavbulyan.blog.entities.Article;
import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.exceptions.ArticleNotFoundException;
import org.blbulyandavbulyan.blog.repositories.ArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticlesService {
    private final ArticleRepository articleRepository;
    private final UserService userService;
    public ArticlePublished publishArticle(String title, String text, User publisher){
        Article article = new Article(publisher, title, text);
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

    public Page<ArticleInfoDTO> getInfoAboutAll(int pageSize, int pageNumber) {
        return articleRepository.findAllPagesBy(ArticleInfoDTO.class, PageRequest.of(pageNumber, pageSize));
    }

    public boolean deleteById(Long id) {
        if(articleRepository.existsById(id)){
            articleRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
