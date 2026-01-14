package com.earthlocals.earthlocals.service.gestioneutente.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
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

}
