package com.earthlocals.earthlocals.utility.constraints;

import com.earthlocals.earthlocals.utility.interfaces.DateOverlapVerifier;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateValidator
        implements ConstraintValidator<DateOverlap, DateOverlapVerifier> {

    private DateOverlap overlap;

    @Override
    public void initialize(DateOverlap constraintAnnotation) {
        overlap = constraintAnnotation;
    }

    @Override
    public boolean isValid(DateOverlapVerifier verifier, ConstraintValidatorContext context){
        var isValid = !verifier.isDateOverlapping();

        if(!isValid){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(overlap.message())
                    .addPropertyNode(overlap.connectedField()).addConstraintViolation();
        }
        return isValid;
    }
}
