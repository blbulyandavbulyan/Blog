package org.blbulyandavbulyan.blog.annotations.validation.article;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Данная аннотация нужна для валидации текста статьи
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.RECORD_COMPONENT, ElementType.PARAMETER, ElementType.FIELD})
@Constraint(validatedBy = {})
@NotBlank
@Size(max = 5000)
public @interface ValidArticleText {
    String message() default "Invalid text";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
