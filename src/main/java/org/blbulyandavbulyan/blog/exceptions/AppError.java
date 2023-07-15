package org.blbulyandavbulyan.blog.exceptions;

import java.util.Date;

/**
 * Данная запись используется для отправки в виде JSON пользователю, в случае ошибки
 * @param status статус код
 * @param message сообщение
 * @param timestamp время ошибки
 */
public record AppError (int status, String message, Date timestamp){
    /**
     * Создаёт экземпляр, в качестве timestamp используется текущее время
     * @param status статус код
     * @param message сообщение об ошибке
     */
    public AppError(int status, String message) {
        this(status, message, new Date());
    }
}
