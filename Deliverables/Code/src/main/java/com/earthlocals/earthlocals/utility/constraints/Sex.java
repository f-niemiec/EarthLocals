package com.earthlocals.earthlocals.utility.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SexValidator.class)
@Documented
public @interface Sex {
    String message() default "Il sesso deve essere M o F";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String connectedField() default "sesso";
}
