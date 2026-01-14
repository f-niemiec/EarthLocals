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
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

// TODO: Change DataJpaTest
@DataJpaTest
@ExtendWith(MockitoExtension.class)
public class UtenteDTOUnitTest {
    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        var clock = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC);
        var factory = Validation.byDefaultProvider().configure().clockProvider(() -> clock).buildValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void UtenteDTOValid() {
        var utenteDTO = new UtenteDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(utenteDTO);
        assertTrue(constraintValidations.isEmpty());
    }

    @Test
    void UtenteDTONullNomeFails() {
        var utenteDTO = new UtenteDTO(
                null,
                "cognome",
                "utente@email.com",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(utenteDTO);
        var constraintValidationsNome = validator.validateProperty(utenteDTO, "nome");
        assertFalse(constraintValidationsNome.isEmpty());
        assertEquals(constraintValidations, constraintValidationsNome);
    }

    @Test
    void UtenteDTOEmptyNomeFails() {
        var utenteDTO = new UtenteDTO(
                "",
                "cognome",
                "utente@email.com",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(utenteDTO);
        var constraintValidationsNome = validator.validateProperty(utenteDTO, "nome");
        assertFalse(constraintValidationsNome.isEmpty());
        assertEquals(constraintValidations, constraintValidationsNome);
    }

    @Test
    void UtenteDTONullCognomeFails() {
        var utenteDTO = new UtenteDTO(
                "nome",
                null,
                "utente@email.com",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(utenteDTO);
        var constraintValidationsCognome = validator.validateProperty(utenteDTO, "cognome");
        assertFalse(constraintValidationsCognome.isEmpty());
        assertEquals(constraintValidations, constraintValidationsCognome);
    }

    @Test
    void UtenteDTOEmptyCognomeFails() {
        var utenteDTO = new UtenteDTO(
                "nome",
                "",
                "utente@email.com",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(utenteDTO);
        var constraintValidationsCognome = validator.validateProperty(utenteDTO, "cognome");
        assertFalse(constraintValidationsCognome.isEmpty());
        assertEquals(constraintValidations, constraintValidationsCognome);
    }

    @Test
    void UtenteDTONullEmailFails() {
        var utenteDTO = new UtenteDTO(
                "nome",
                "cognome",
                null,
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(utenteDTO);
        var constraintValidationsEmail = validator.validateProperty(utenteDTO, "email");
        assertFalse(constraintValidationsEmail.isEmpty());
        assertEquals(constraintValidations, constraintValidationsEmail);
    }

    @Test
    void UtenteDTOEmptyEmailFails() {
        var utenteDTO = new UtenteDTO(
                "nome",
                "cognome",
                "",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(utenteDTO);
        var constraintValidationsEmail = validator.validateProperty(utenteDTO, "email");
        assertFalse(constraintValidationsEmail.isEmpty());
        assertEquals(constraintValidations, constraintValidationsEmail);
    }

    @Test
    void UtenteDTOMissingChiocciolaEmailFails() {
        var utenteDTO = new UtenteDTO(
                "nome",
                "cognome",
                "user.domain",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(utenteDTO);
        var constraintValidationsEmail = validator.validateProperty(utenteDTO, "email");
        assertFalse(constraintValidationsEmail.isEmpty());
        assertEquals(constraintValidations, constraintValidationsEmail);
    }

    @Test
    void UtenteDTOMissingUsernameEmailFails() {
        var utenteDTO = new UtenteDTO(
                "nome",
                "cognome",
                "@domain",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(utenteDTO);
        var constraintValidationsEmail = validator.validateProperty(utenteDTO, "email");
        assertFalse(constraintValidationsEmail.isEmpty());
        assertEquals(constraintValidations, constraintValidationsEmail);
    }

    @Test
    void UtenteDTOMissingDomainEmailFails() {
        var utenteDTO = new UtenteDTO(
                "nome",
                "cognome",
                "utente@",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(utenteDTO);
        var constraintValidationsEmail = validator.validateProperty(utenteDTO, "email");
        assertFalse(constraintValidationsEmail.isEmpty());
        assertEquals(constraintValidations, constraintValidationsEmail);
    }

    @Test
    void UtenteDTONullNazionalitaFails() {
        var utenteDTO = new UtenteDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abcYZ17!?",
                "abcYZ17!?",
                null,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(utenteDTO);
        var constraintValidationsNazionalita = validator.validateProperty(utenteDTO, "nazionalita");
        assertFalse(constraintValidationsNazionalita.isEmpty());
        assertEquals(constraintValidations, constraintValidationsNazionalita);
    }

    @Test
    void UtenteDTONegativeNazionalitaFails() {
        var utenteDTO = new UtenteDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abcYZ17!?",
                "abcYZ17!?",
                -1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(utenteDTO);
        var constraintValidationsNazionalita = validator.validateProperty(utenteDTO, "nazionalita");
        assertFalse(constraintValidationsNazionalita.isEmpty());
        assertEquals(constraintValidations, constraintValidationsNazionalita);
    }

    @Test
    void UtenteDTOZeroNazionalitaSucceeds() {
        var utenteDTO = new UtenteDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abcYZ17!?",
                "abcYZ17!?",
                0,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(utenteDTO);
        assertTrue(constraintValidations.isEmpty());
    }


    @Test
    void UtenteDTOPasswordMissingLowercaseFails() {
        var utenteDTO = new UtenteDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "ABCYZ17!?",
                "ABCYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidationsPassword = validator.validateProperty(utenteDTO, "password");
        var constraintValidations = validator.validate(utenteDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void UtenteDTOPasswordMissingUppercaseFails() {
        var utenteDTO = new UtenteDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abcyz17!?",
                "abcyz17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidationsPassword = validator.validateProperty(utenteDTO, "password");
        var constraintValidations = validator.validate(utenteDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void UtenteDTOPasswordMissingNumberFails() {
        var utenteDTO = new UtenteDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abcYZIT!?",
                "abcYZIT!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidationsPassword = validator.validateProperty(utenteDTO, "password");
        var constraintValidations = validator.validate(utenteDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void UtenteDTOPasswordMissingSpecialCharacterFails() {
        var utenteDTO = new UtenteDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abcYZ17IP",
                "abcYZ17IP",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidationsPassword = validator.validateProperty(utenteDTO, "password");
        var constraintValidations = validator.validate(utenteDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void UtenteDTOPasswordLessThan8CharactersFails() {
        var utenteDTO = new UtenteDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abZ17!?",
                "abZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidationsPassword = validator.validateProperty(utenteDTO, "password");
        var constraintValidations = validator.validate(utenteDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void UtenteDTOPassword8CharactersSucceeds() {
        var utenteDTO = new UtenteDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abYZ17!?",
                "abYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(utenteDTO);
        assertTrue(constraintValidations.isEmpty());
    }

    @Test
    void UtenteDTOPasswordNullFails() {
        var utenteDTO = new UtenteDTO(
                "nome",
                "cognome",
                "utente@email.com",
                null,
                null,
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );
        var constraintValidationsPassword = validator.validateProperty(utenteDTO, "password");
        var constraintValidations = validator.validate(utenteDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);

    }

    @Test
    void UtenteDTOPasswordBlankFails() {
        var utenteDTO = new UtenteDTO(
                "nome",
                "cognome",
                "utente@email.com",
                " ".repeat(8),
                " ".repeat(8),
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );
        var constraintValidationsPassword = validator.validateProperty(utenteDTO, "password");
        var constraintValidations = validator.validate(utenteDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void UtenteDTOPasswordInvalidCharactersFails() {
        var utenteDTO = new UtenteDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "我们".repeat(8),
                "我们".repeat(8),
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );
        var constraintValidationsPassword = validator.validateProperty(utenteDTO, "password");
        var constraintValidations = validator.validate(utenteDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void UtenteDTONonMatchingPasswordFails() {
        var utenteDTO = new UtenteDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abcYZ17!?",
                "nonMatchingPassword",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidationsPassword = validator.validate(utenteDTO, UtenteDTO.UtenteDTOPasswordsMatchGroup.class);
        var constraintValidations = validator.validate(utenteDTO, Default.class, UtenteDTO.UtenteDTOPasswordsMatchGroup.class);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }


}
