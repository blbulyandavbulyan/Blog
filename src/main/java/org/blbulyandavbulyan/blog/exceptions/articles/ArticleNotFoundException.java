package org.blbulyandavbulyan.blog.exceptions.articles;

import org.blbulyandavbulyan.blog.exceptions.ResourceNofFoundException;

public class ArticleNotFoundException extends ResourceNofFoundException {
    public ArticleNotFoundException(String message) {
        super(message);
    }
}
