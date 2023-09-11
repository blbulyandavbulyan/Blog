package org.blbulyandavbulyan.blog.annotations.validation.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Эта аннотация предназначена для выставления на DTO в которых содержится ID существующего пользователя
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.RECORD_COMPONENT, ElementType.PARAMETER, ElementType.FIELD})
@Constraint(validatedBy = {})
@NotNull
@Min(1)
@Max(Long.MAX_VALUE)
public @interface ValidUserId {
    String message() default "Invalid user id!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
