package org.blbulyandavbulyan.blog.exceptions.articles;

import org.blbulyandavbulyan.blog.exceptions.ResourceNofFoundException;

/**
 * Данное исключение бросается, в случае если статья по заданному ИД не была найдена
 */
public class ArticleNotFoundException extends ResourceNofFoundException {
    public ArticleNotFoundException(String message) {
        super(message);
    }
}
