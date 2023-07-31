package org.blbulyandavbulyan.blog.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.article.ArticleDto;
import org.blbulyandavbulyan.blog.dtos.article.ArticleForPublishing;
import org.blbulyandavbulyan.blog.dtos.article.ArticleInfoDTO;
import org.blbulyandavbulyan.blog.dtos.article.ArticlePublished;
import org.blbulyandavbulyan.blog.services.ArticlesService;
import org.blbulyandavbulyan.blog.specs.ArticleSpecification;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

/**
 * Контроллер для статей
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/articles")
public class ArticlesController {
    /**
     * Ссылка на сервис для статей
     */
    private final ArticlesService articlesService;

    /**
     * Получает статью по ИД
     * @param id ид статьи
     * @return найденную статью типа ArticleDto
     */
    @GetMapping("/{id}")
    public ArticleDto getById(@PathVariable Long id) {
        return articlesService.getById(id);
    }

    /**
     * Удаляет статью по ид
     * @param id ид статьи, которую нужно удалить
     */
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteArticleById(@PathVariable Long id) {
        articlesService.deleteById(id);
    }

    /**
     * Публикует статью
     * @param articleForPublishing статья, которую нужно опубликовать
     * @param principal ссылка на Principal, в котором содержатся данные об авторизованном пользователе
     * @return информацию об опубликованной статье
     */
    @Secured("ROLE_PUBLISHER")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArticlePublished publishArticle(@RequestBody ArticleForPublishing articleForPublishing, Principal principal) {
        return articlesService.publishArticle(articleForPublishing, principal.getName());
    }

    /**
     * Получает краткую информацию о всех статьях в виде страницы
     * @param pageSize размер страницы
     * @param pageNumber номер страницы
     * @return найденную страницу с краткой информацией о статьях
     */
    @GetMapping("/info/all")
    public Page<ArticleInfoDTO> getInfoAboutAllArticles(@RequestParam(defaultValue = "5", name = "s") Integer pageSize, @RequestParam(defaultValue = "1", name = "p") Integer pageNumber, @RequestParam Map<String, String> requestParams) {
        return articlesService.getInfoAboutAll(new ArticleSpecification(requestParams).getArticleSpecification(), pageSize, pageNumber - 1);
    }
}
