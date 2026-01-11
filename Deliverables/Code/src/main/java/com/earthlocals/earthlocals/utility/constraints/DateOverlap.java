package com.earthlocals.earthlocals.utility.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateValidator.class)
@Documented
public @interface DateOverlap {
    String message() default "La data di fine deve essere più in avanti rispetto" +
            " alla data di inizio";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String connectedField() default "okDates";
}
