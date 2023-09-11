package org.blbulyandavbulyan.blog.annotations.validation.article;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Constraint(validatedBy = {})
@NotBlank
@Size(max = 255)
public @interface ValidArticleTitle {
    String message() default "Invalid title";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
