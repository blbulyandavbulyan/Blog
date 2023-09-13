package org.blbulyandavbulyan.blog.annotations.validation.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Данная аннотация ставится на DTO в которых содержится сырой пароль
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.RECORD_COMPONENT, ElementType.PARAMETER, ElementType.FIELD})
@Constraint(validatedBy = {})
@NotBlank
@Size(max = 100)
public @interface ValidRawPassword {
    String message() default "Invalid raw password!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
