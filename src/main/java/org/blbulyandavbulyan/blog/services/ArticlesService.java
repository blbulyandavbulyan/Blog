package org.blbulyandavbulyan.blog.services;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.article.*;
import org.blbulyandavbulyan.blog.entities.Article;
import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.exceptions.articles.ArticleNotFoundException;
import org.blbulyandavbulyan.blog.repositories.ArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы со статьями
 */
@Service
@RequiredArgsConstructor
public class ArticlesService {
    /**
     * Репозиторий статей
     */
    private final ArticleRepository articleRepository;
    /**
     * Сервис пользователей
     */
    private final UserService userService;

    /**
     * Публикует статью от имени данного пользователя
     * @param title название статьи
     * @param text текст статьи
     * @param publisher имя публикующего пользователя
     * @return объект, содержащий информацию об опубликованной статьей
     */
    private ArticlePublished publishArticle(String title, String text, User publisher){
        Article article = new Article(publisher, title, text);
        articleRepository.save(article);
        return new ArticlePublished(article.getArticleId());
    }

    /**
     * Получает DTO для статьи по ИД
     * @param id ИД по которому нужно загрузить данную статью
     * @return DTO, содержащее статью
     */
    public ArticleDto getById(Long id) {
        return articleRepository.findByArticleId(id, ArticleDto.class).orElseThrow(()->new ArticleNotFoundException("Article with id " + id + " not found"));
    }

    /**
     * Публикует статью
     * @param articleForPublishing данные, которые будут в созданной статье
     * @param publisherName имя публикатора
     * @return информацию, об опубликованной статье
     */
    public ArticlePublished publishArticle(ArticleForPublishing articleForPublishing, String publisherName) {
        return publishArticle(articleForPublishing.title(), articleForPublishing.text(),
                userService.getReferenceByName(publisherName));
    }

    /**
     * Получает информацию о статьях в виде страницы
     * @param spec спецификация, по которой будут фильтроваться статьи
     * @param pageSize размер страницы
     * @param pageNumber номер страницы(начиная с 0)
     * @return страницу, содержащую статьи
     */
    public Page<ArticleInfoDTO> getInfoAboutAll(Specification<Article> spec, int pageSize, int pageNumber) {
        return articleRepository.findBy(spec, q-> q.project("articleId", "title", "publishDate", "publisher.name").as(ArticleInfoDTO.class).page(PageRequest.of(pageNumber, pageSize)));
    }

    /**
     * Удаляет статью по ИД
     * @param id ИД статьи, которую нужно удалить
     */
    public void deleteById(Long id) {
        if(articleRepository.existsById(id)) articleRepository.deleteById(id);
        else throw new ArticleNotFoundException("Article with id " + id + " not found");
    }

    /**
     * Получает ссылку на статью
     * @param articleId ИД статьи, на которую нужно получить ссылку
     * @return ссылку на статью, по заданному ИД
     * @throws ArticleNotFoundException если статья не найдена по данному ИД
     */
    public Article getReferenceById(Long articleId) {
        if(!existsById(articleId))
            throw new ArticleNotFoundException("Article with id " + articleId + " not found!");
        return articleRepository.getReferenceById(articleId);
    }

    /**
     * Проверяет, существует ли статья по заданному ИД
     * @param id ИД для проверки статьи на существование
     * @return true если существует
     */
    public boolean existsById(Long id) {
        return articleRepository.existsById(id);
    }
}
