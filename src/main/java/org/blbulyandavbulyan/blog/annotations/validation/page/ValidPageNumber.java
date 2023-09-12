package org.blbulyandavbulyan.blog.annotations.validation.page;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.RECORD_COMPONENT, ElementType.PARAMETER, ElementType.FIELD})
@Constraint(validatedBy = {})
@NotNull
@Min(1)
@Max(Integer.MAX_VALUE)
public @interface ValidPageNumber {
    String message() default "Invalid page number";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
