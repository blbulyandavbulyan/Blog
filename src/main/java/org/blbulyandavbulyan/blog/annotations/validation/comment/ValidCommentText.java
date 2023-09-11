package org.blbulyandavbulyan.blog.annotations.validation.comment;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Constraint(validatedBy = {})
@NotBlank
@Size(max = 2000)
public @interface ValidCommentText {
    String message() default "Invalid comment text";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
