package com.earthlocals.earthlocals.utility.constraints;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Documented
public @interface PasswordMatches {
    String message() default "Le password non corrispondono";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String connectedField() default "matchingPassword";
}
