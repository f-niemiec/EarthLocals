package com.earthlocals.earthlocals.utility.constraints;


import com.earthlocals.earthlocals.utility.interfaces.PasswordMatchingVerifiable;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator
        implements ConstraintValidator<PasswordMatches, PasswordMatchingVerifiable> {

    private PasswordMatches passwordMatches;

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        passwordMatches = constraintAnnotation;
    }

    @Override
    public boolean isValid(PasswordMatchingVerifiable user, ConstraintValidatorContext context) {
        var isValid = user.isPasswordMatching();
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(passwordMatches.message())
                    .addPropertyNode(passwordMatches.connectedField()).addConstraintViolation();
        }
        return isValid;
    }
}
