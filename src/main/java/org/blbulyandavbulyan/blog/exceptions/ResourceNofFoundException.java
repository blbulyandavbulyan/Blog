package org.blbulyandavbulyan.blog.exceptions;

public class ResourceNofFoundException extends BadRequestException{
    public ResourceNofFoundException(String message) {
        super(message);
    }
}
