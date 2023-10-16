package org.blbulyandavbulyan.blog.services.reactions;

import org.blbulyandavbulyan.blog.entities.Article;
import org.blbulyandavbulyan.blog.entities.ArticleReaction;
import org.blbulyandavbulyan.blog.repositories.reactions.ArticleReactionRepository;
import org.blbulyandavbulyan.blog.services.ArticleService;
import org.blbulyandavbulyan.blog.services.UserService;
import org.springframework.stereotype.Service;

@Service
public class ArticleReactionService extends AbstractReactionService<ArticleReaction, Article, ArticleReactionRepository>{
    public ArticleReactionService(UserService userService, ArticleService articleService, ArticleReactionRepository repository) {
        super(userService, repository, ArticleReaction::new, articleService::getReferenceById);
    }
}
