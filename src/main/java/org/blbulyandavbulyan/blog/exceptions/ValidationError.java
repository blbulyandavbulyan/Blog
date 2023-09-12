package org.blbulyandavbulyan.blog.exceptions;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ValidationError extends AppError{
    private final Map<String, List<String>> errorDetails;

    public ValidationError(int status, String message, Map<String, List<String>> errorDetails) {
        super(status, message);
        this.errorDetails = errorDetails;
    }

}
