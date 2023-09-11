package org.blbulyandavbulyan.blog.annotations.validation.article;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Constraint(validatedBy = {})
@NotNull
@Min(1)
@Max(Long.MAX_VALUE)
public @interface ValidArticleId {
    String message() default "Invalid article id";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
