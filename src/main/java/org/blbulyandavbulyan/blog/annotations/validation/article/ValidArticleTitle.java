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
 * Данная аннотация нужна для валидации названия статьи
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.RECORD_COMPONENT, ElementType.PARAMETER, ElementType.FIELD})
@Constraint(validatedBy = {})
@NotBlank
@Size(max = 255)
public @interface ValidArticleTitle {
    String message() default "Invalid title";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
