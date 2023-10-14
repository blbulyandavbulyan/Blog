package org.blbulyandavbulyan.blog.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.annotations.validation.article.ValidArticleId;
import org.blbulyandavbulyan.blog.annotations.validation.page.ValidPageNumber;
import org.blbulyandavbulyan.blog.annotations.validation.page.ValidPageSize;
import org.blbulyandavbulyan.blog.dtos.article.ArticleInfoDTO;
import org.blbulyandavbulyan.blog.dtos.article.ArticlePublishedResponse;
import org.blbulyandavbulyan.blog.dtos.article.ArticleResponse;
import org.blbulyandavbulyan.blog.dtos.article.CreateArticleRequest;
import org.blbulyandavbulyan.blog.services.ArticleService;
import org.blbulyandavbulyan.blog.specs.ArticleSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

/**
 * Контроллер для статей
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/articles")
@Validated
public class ArticleController {
    /**
     * Ссылка на сервис для статей
     */
    private final ArticleService articleService;

    /**
     * Получает статью по ИД
     * @param id ид статьи
     * @return найденную статью типа ArticleDto
     */
    @GetMapping("/{id}")
    public ArticleResponse getById(@ValidArticleId @PathVariable Long id) {
        return articleService.getById(id);
    }

    /**
     * Удаляет статью по ид
     * @param id ид статьи, которую нужно удалить
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteArticleById(@ValidArticleId @PathVariable Long id, Authentication authentication) {
        articleService.deleteById(id, authentication);
    }

    /**
     * Публикует статью
     * @param createArticleRequest статья, которую нужно опубликовать
     * @param principal ссылка на Principal, в котором содержатся данные об авторизованном пользователе
     * @return информацию об опубликованной статье
     */
    @Secured("ROLE_PUBLISHER")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArticlePublishedResponse publishArticle(@Validated @RequestBody CreateArticleRequest createArticleRequest, Principal principal) {
        return articleService.publishArticle(createArticleRequest, principal.getName());
    }

    /**
     * Получает краткую информацию о всех статьях в виде страницы
     * @param pageSize размер страницы
     * @param pageNumber номер страницы
     * @return найденную страницу с краткой информацией о статьях
     */
    @GetMapping("/info/all")
    public Page<ArticleInfoDTO> getInfoAboutAllArticles(@ValidPageSize @RequestParam(defaultValue = "5", name = "s") Integer pageSize,
                                                        @ValidPageNumber @RequestParam(defaultValue = "1", name = "p") Integer pageNumber,
                                                        @RequestParam Map<String, String> requestParams) {
        return articleService.getInfoAboutAll(new ArticleSpecifications(requestParams).getArticleSpecification(), pageSize, pageNumber - 1);
    }
    @PatchMapping("/{articleId}")
    public void updateArticle(@ValidArticleId @PathVariable Long articleId,
                              @Validated @RequestBody CreateArticleRequest createArticleRequest,
                              Principal principal){
        articleService.updateArticle(articleId, createArticleRequest.title(), createArticleRequest.text(), principal.getName());
    }
}
