package org.blbulyandavbulyan.blog.services.reactions;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.entities.Article;
import org.blbulyandavbulyan.blog.entities.ArticleReaction;
import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.repositories.ArticleReactionRepository;
import org.blbulyandavbulyan.blog.services.ArticleService;
import org.blbulyandavbulyan.blog.services.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleReactionService {
    private final ArticleService articleService;
    private final ArticleReactionRepository articleReactionRepository;
    private final UserService userService;
    /**
     * Данный метод удаляет реакцию от данного пользователя к данной статье
     * @param id id статьи, с которой нужно убрать реакцию
     * @param username имя пользователя, от которого нужно убрать реакцию
     */
    public void removeReaction(Long id, String username){
        articleReactionRepository.deleteByArticleAndLiker(articleService.getReferenceById(id), userService.getReferenceByName(username));
    }
    public void react(Long id, String username, boolean liked){
        Article article = articleService.getReferenceById(id);
        User liker = userService.getReferenceByName(username);
        ArticleReaction reaction = articleReactionRepository.findByArticleAndLiker(article, liker)
                .orElseGet(() -> new ArticleReaction(article, liker));
        reaction.setLiked(liked);
        articleReactionRepository.save(reaction);
    }
}
