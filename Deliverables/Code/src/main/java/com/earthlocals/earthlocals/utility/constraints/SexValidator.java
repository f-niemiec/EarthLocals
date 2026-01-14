package com.earthlocals.earthlocals.utility.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SexValidator implements ConstraintValidator<Sex, Character> {
    @Override
    public boolean isValid(Character value, ConstraintValidatorContext context) {
        return value == 'M' || value == 'F';
    }
}
