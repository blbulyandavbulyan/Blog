package org.blbulyandavbulyan.blog.exceptions;

import java.util.Date;

public record AppError (int status, String message, Date timestamp){
    public AppError(int status, String message) {
        this(status, message, new Date());
    }
}
