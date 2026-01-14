package com.earthlocals.earthlocals.service.gestioneutente.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

// TODO: Change DataJpaTest
@DataJpaTest
@ExtendWith(MockitoExtension.class)
public class ResetPasswordDTOUnitTest {
    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        var clock = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC);
        var factory = Validation.byDefaultProvider().configure().clockProvider(() -> clock).buildValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void ResetPasswordDTOValid() {
        var resetPasswordDTO = new ResetPasswordDTO(
                "abcYZ17!?",
                "abcYZ17!?",
                "token"
        );
        var constraintValidations = validator.validate(resetPasswordDTO);
        assertTrue(constraintValidations.isEmpty());
    }


    @Test
    void resetPasswordDTOPasswordMissingLowercaseFails() {
        var resetPasswordDTO = new ResetPasswordDTO(
                "ABCYZ17!?",
                "ABCYZ17!?",
                "token"
        );

        var constraintValidationsPassword = validator.validateProperty(resetPasswordDTO, "newPassword");
        var constraintValidations = validator.validate(resetPasswordDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void resetPasswordDTOPasswordMissingUppercaseFails() {
        var resetPasswordDTO = new ResetPasswordDTO(
                "abcyz17!?",
                "abcyz17!?",
                "token"
        );

        var constraintValidationsPassword = validator.validateProperty(resetPasswordDTO, "newPassword");
        var constraintValidations = validator.validate(resetPasswordDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void resetPasswordDTOPasswordMissingNumberFails() {
        var resetPasswordDTO = new ResetPasswordDTO(
                "abcYZIT!?",
                "abcYZIT!?",
                "token"
        );

        var constraintValidationsPassword = validator.validateProperty(resetPasswordDTO, "newPassword");
        var constraintValidations = validator.validate(resetPasswordDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void resetPasswordDTOPasswordMissingSpecialCharacterFails() {
        var resetPasswordDTO = new ResetPasswordDTO(
                "abcYZ17IP",
                "abcYZ17IP",
                "token"
        );

        var constraintValidationsPassword = validator.validateProperty(resetPasswordDTO, "newPassword");
        var constraintValidations = validator.validate(resetPasswordDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void resetPasswordDTOPasswordLessThan8CharactersFails() {
        var resetPasswordDTO = new ResetPasswordDTO(
                "abZ17!?",
                "abZ17!?",
                "token"
        );

        var constraintValidationsPassword = validator.validateProperty(resetPasswordDTO, "newPassword");
        var constraintValidations = validator.validate(resetPasswordDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void resetPasswordDTOPassword8CharactersSucceeds() {
        var resetPasswordDTO = new ResetPasswordDTO(
                "abYZ17!?",
                "abYZ17!?",
                "token"
        );

        var constraintValidations = validator.validate(resetPasswordDTO);
        assertTrue(constraintValidations.isEmpty());
    }

    @Test
    void resetPasswordDTOPasswordNullFails() {
        var resetPasswordDTO = new ResetPasswordDTO(
                null,
                null,
                "token"
        );
        var constraintValidationsPassword = validator.validateProperty(resetPasswordDTO, "newPassword");
        var constraintValidations = validator.validate(resetPasswordDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);

    }

    @Test
    void resetPasswordDTOPasswordBlankFails() {
        var resetPasswordDTO = new ResetPasswordDTO(
                " ".repeat(8),
                " ".repeat(8),
                "token"
        );
        var constraintValidationsPassword = validator.validateProperty(resetPasswordDTO, "newPassword");
        var constraintValidations = validator.validate(resetPasswordDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void resetPasswordDTOPasswordInvalidCharactersFails() {
        var resetPasswordDTO = new ResetPasswordDTO(
                "我们".repeat(8),
                "我们".repeat(8),
                "token"
        );
        var constraintValidationsPassword = validator.validateProperty(resetPasswordDTO, "newPassword");
        var constraintValidations = validator.validate(resetPasswordDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void resetPasswordDTONonMatchingPasswordFails() {
        var resetPasswordDTO = new ResetPasswordDTO(
                "abcYZ17!?",
                "nonMatchingPassword",
                "token"
        );

        var constraintValidationsPassword = validator.validate(resetPasswordDTO, ResetPasswordDTO.ResetPasswordDTOPasswordsMatchGroup.class);
        var constraintValidations = validator.validate(resetPasswordDTO, Default.class, ResetPasswordDTO.ResetPasswordDTOPasswordsMatchGroup.class);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void ResetPasswordDTONullTokenFails() {
        var resetPasswordDTO = new ResetPasswordDTO(
                "abcYZ17!?",
                "abcYZ17!?",
                null
        );
        var constraintValidations = validator.validate(resetPasswordDTO);
        var constraintValidationsToken = validator.validateProperty(resetPasswordDTO, "token");
        assertFalse(constraintValidationsToken.isEmpty());
        assertEquals(constraintValidations, constraintValidationsToken);
    }

    @Test
    void ResetPasswordDTOEmptyTokenFails() {
        var resetPasswordDTO = new ResetPasswordDTO(
                "abcYZ17!?",
                "abcYZ17!?",
                ""
        );
        var constraintValidations = validator.validate(resetPasswordDTO);
        var constraintValidationsToken = validator.validateProperty(resetPasswordDTO, "token");
        assertFalse(constraintValidationsToken.isEmpty());
        assertEquals(constraintValidations, constraintValidationsToken);
    }

    @Test
    void ResetPasswordDTOBlankTokenFails() {
        var resetPasswordDTO = new ResetPasswordDTO(
                "abcYZ17!?",
                "abcYZ17!?",
                " ".repeat(8)
        );
        var constraintValidations = validator.validate(resetPasswordDTO);
        var constraintValidationsToken = validator.validateProperty(resetPasswordDTO, "token");
        assertFalse(constraintValidationsToken.isEmpty());
        assertEquals(constraintValidations, constraintValidationsToken);
    }
}
