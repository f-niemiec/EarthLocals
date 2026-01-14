package com.earthlocals.earthlocals.service.gestioneutente.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

// TODO: Change DataJpaTest
@ExtendWith(SpringExtension.class)
public class EditPasswordDTOUnitTest {
    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        var clock = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC);
        var factory = Validation.byDefaultProvider().configure().clockProvider(() -> clock).buildValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void EditPasswordDTOValid() {
        var editPasswordDTO = new EditPasswordDTO(
                "oldPassword",
                "abcYZ17!?",
                "abcYZ17!?"
        );

        var constraintValidations = validator.validate(editPasswordDTO);
        assertTrue(constraintValidations.isEmpty());
    }

    @Test
    void EditPasswordDTONullCurrentPasswordFails() {
        var editPasswordDTO = new EditPasswordDTO(
                null,
                "abcYZ17!?",
                "abcYZ17!?"
        );

        var constraintValidationsPassword = validator.validateProperty(editPasswordDTO, "currentPassword");
        var constraintValidations = validator.validate(editPasswordDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void EditPasswordDTOEmptyCurrentPasswordFails() {
        var editPasswordDTO = new EditPasswordDTO(
                "",
                "abcYZ17!?",
                "abcYZ17!?"
        );

        var constraintValidationsPassword = validator.validateProperty(editPasswordDTO, "currentPassword");
        var constraintValidations = validator.validate(editPasswordDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void EditPasswordDTOBlankCurrentPasswordFails() {
        var editPasswordDTO = new EditPasswordDTO(
                " ",
                "abcYZ17!?",
                "abcYZ17!?"
        );

        var constraintValidationsPassword = validator.validateProperty(editPasswordDTO, "currentPassword");
        var constraintValidations = validator.validate(editPasswordDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }


    @Test
    void EditPasswordDTOPasswordMissingLowercaseFails() {
        var editPasswordDTO = new EditPasswordDTO(
                "oldPassword",
                "ABCYZ17!?",
                "ABCYZ17!?"
        );

        var constraintValidationsPassword = validator.validateProperty(editPasswordDTO, "newPassword");
        var constraintValidations = validator.validate(editPasswordDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void EditPasswordDTOPasswordMissingUppercaseFails() {
        var editPasswordDTO = new EditPasswordDTO(
                "oldPassword",
                "abcyz17!?",
                "abcyz17!?"
        );

        var constraintValidationsPassword = validator.validateProperty(editPasswordDTO, "newPassword");
        var constraintValidations = validator.validate(editPasswordDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void EditPasswordDTOPasswordMissingNumberFails() {
        var editPasswordDTO = new EditPasswordDTO(
                "oldPassword",
                "abcYZIT!?",
                "abcYZIT!?"
        );

        var constraintValidationsPassword = validator.validateProperty(editPasswordDTO, "newPassword");
        var constraintValidations = validator.validate(editPasswordDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void EditPasswordDTOPasswordMissingSpecialCharacterFails() {
        var editPasswordDTO = new EditPasswordDTO(
                "oldPassword",
                "abcYZ17IP",
                "abcYZ17IP"
        );

        var constraintValidationsPassword = validator.validateProperty(editPasswordDTO, "newPassword");
        var constraintValidations = validator.validate(editPasswordDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void EditPasswordDTOPasswordLessThan8CharactersFails() {
        var editPasswordDTO = new EditPasswordDTO(
                "oldPassword",
                "abZ17!?",
                "abZ17!?"
        );

        var constraintValidationsPassword = validator.validateProperty(editPasswordDTO, "newPassword");
        var constraintValidations = validator.validate(editPasswordDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void EditPasswordDTOPassword8CharactersSucceeds() {
        var editPasswordDTO = new EditPasswordDTO(
                "oldPassword",
                "abYZ17!?",
                "abYZ17!?"
        );

        var constraintValidations = validator.validate(editPasswordDTO);
        assertTrue(constraintValidations.isEmpty());
    }

    @Test
    void EditPasswordDTOPasswordNullFails() {
        var editPasswordDTO = new EditPasswordDTO(
                "oldPassword",
                null,
                null
        );
        var constraintValidationsPassword = validator.validateProperty(editPasswordDTO, "newPassword");
        var constraintValidations = validator.validate(editPasswordDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);

    }

    @Test
    void EditPasswordDTOPasswordBlankFails() {
        var editPasswordDTO = new EditPasswordDTO(
                "oldPassword",
                " ".repeat(8),
                " ".repeat(8)
        );
        var constraintValidationsPassword = validator.validateProperty(editPasswordDTO, "newPassword");
        var constraintValidations = validator.validate(editPasswordDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void EditPasswordDTOPasswordInvalidCharactersFails() {
        var editPasswordDTO = new EditPasswordDTO(
                "oldPassword",
                "我们".repeat(8),
                "我们".repeat(8)
        );
        var constraintValidationsPassword = validator.validateProperty(editPasswordDTO, "newPassword");
        var constraintValidations = validator.validate(editPasswordDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void EditPasswordDTONonMatchingPasswordFails() {
        var editPasswordDTO = new EditPasswordDTO(
                "oldPassword",
                "abcYZ17!?",
                "nonMatchingPassword"
        );

        var constraintValidationsPassword = validator.validate(editPasswordDTO, EditPasswordDTO.EditPasswordDTOPasswordsMatchGroup.class);
        var constraintValidations = validator.validate(editPasswordDTO, Default.class, EditPasswordDTO.EditPasswordDTOPasswordsMatchGroup.class);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

}
