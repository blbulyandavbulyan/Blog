package org.blbulyandavbulyan.blog.services;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.entities.Article;
import org.blbulyandavbulyan.blog.entities.ArticleReaction;
import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.repositories.ArticleReactionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleReactionService {
    private final ArticleService articleService;
    private final ArticleReactionRepository articleReactionRepository;
    private final UserService userService;

    /**
     * Данный метод лайкает статью от имени заданного пользователя
     * @param id ИД статьи, которую нужно лайкнуть
     * @param username имя пользователя, от которого будет лайк
     */
    public void likeArticle(Long id, String username) {
        setLiked(id, username, true);
    }

    /**
     * Данный метод дизлайкает статью от имени заданного пользователя
     * @param id id статьи, которую нужно дизлайкнуть
     * @param username имя пользователя, от которого будет дизлайк
     */
    public void dislikeArticle(Long id, String username){
        setLiked(id, username, false);
    }

    /**
     * Данный метод удаляет реакцию от данного пользователя к данной статье
     * @param id id статьи, с которой нужно убрать реакцию
     * @param username имя пользователя, от которого нужно убрать реакцию
     */
    public void removeReaction(Long id, String username){
        articleReactionRepository.deleteByArticleAndLiker(articleService.getReferenceById(id), userService.getReferenceByName(username));
    }
    private void setLiked(Long id, String username, boolean liked){
        Article article = articleService.getReferenceById(id);
        User liker = userService.getReferenceByName(username);
        ArticleReaction reaction = articleReactionRepository.findByArticleAndLiker(article, liker)
                .orElseGet(() -> new ArticleReaction(article, liker));
        reaction.setLiked(liked);
        articleReactionRepository.save(reaction);
    }
}
