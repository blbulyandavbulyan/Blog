package org.blbulyandavbulyan.blog.exceptions;

public class ArticleNotFoundException extends ResourceNofFoundException{
    public ArticleNotFoundException(String message) {
        super(message);
    }
}
