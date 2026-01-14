package com.earthlocals.earthlocals.service.gestioneutente.dto;


import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

// TODO: Change DataJpaTest
@DataJpaTest
@ExtendWith(MockitoExtension.class)
public class VolontarioDTOUnitTest {
    private static Validator validator;
    MultipartFile passport = new MockMultipartFile("passport", "passport".getBytes());

    @BeforeAll
    public static void setUpValidator() {
        var clock = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC);
        var factory = Validation.byDefaultProvider().configure().clockProvider(() -> clock).buildValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void VolontarioDTOValid() {
        var volontarioDTO = new VolontarioDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F',
                "AA000000",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(-1),
                passport
        );
        var constraintValidations = validator.validate(volontarioDTO);
        assertTrue(constraintValidations.isEmpty());
    }

    @Test
    void VolontarioDTONullPassportNumberFails() {
        var volontarioDTO = new VolontarioDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F',
                null,
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(-1),
                passport
        );
        var constraintValidations = validator.validate(volontarioDTO);
        var constraintValidationsPassportNumber = validator.validateProperty(volontarioDTO, "numeroPassaporto");
        assertFalse(constraintValidations.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassportNumber);
    }

    @Test
    void VolontarioDTOBlankPassportNumberFail() {
        var volontarioDTO = new VolontarioDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F',
                " ".repeat(8),
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(-1),
                passport
        );
        var constraintValidations = validator.validate(volontarioDTO);
        var constraintValidationsPassportNumber = validator.validateProperty(volontarioDTO, "numeroPassaporto");
        assertFalse(constraintValidations.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassportNumber);
    }

    @Test
    void VolontarioDTOEmptyPassportNumberFail() {
        var volontarioDTO = new VolontarioDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F',
                "",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(-1),
                passport
        );
        var constraintValidations = validator.validate(volontarioDTO);
        var constraintValidationsPassportNumber = validator.validateProperty(volontarioDTO, "numeroPassaporto");
        assertFalse(constraintValidations.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassportNumber);
    }


    @Test
    void VolontarioDTONullDataScadenzaFails() {
        var volontarioDTO = new VolontarioDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F',
                "AA000000",
                null,
                LocalDate.ofEpochDay(-1),
                passport
        );
        var constraintValidations = validator.validate(volontarioDTO);
        var constraintValidationsDataScadenza = validator.validateProperty(volontarioDTO, "dataScadenzaPassaporto");
        assertFalse(constraintValidationsDataScadenza.isEmpty());
        assertEquals(constraintValidations, constraintValidationsDataScadenza);
    }

    @Test
    void VolontarioDTOPastDataScadenzaFails() {
        var volontarioDTO = new VolontarioDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F',
                "AA000000",
                LocalDate.ofEpochDay(-1),
                LocalDate.ofEpochDay(-1),
                passport
        );
        var constraintValidations = validator.validate(volontarioDTO);
        var constraintValidationsDataScadenza = validator.validateProperty(volontarioDTO, "dataScadenzaPassaporto");
        assertFalse(constraintValidationsDataScadenza.isEmpty());
        assertEquals(constraintValidations, constraintValidationsDataScadenza);
    }

    @Test
    void VolontarioDTOPresentDataScadenzaSucceeds() {
        var volontarioDTO = new VolontarioDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F',
                "AA000000",
                LocalDate.ofEpochDay(0),
                LocalDate.ofEpochDay(-1),
                passport
        );
        var constraintValidations = validator.validate(volontarioDTO);
        assertTrue(constraintValidations.isEmpty());
    }

    @Test
    void VolontarioDTONullDataEmissioneFails() {
        var volontarioDTO = new VolontarioDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F',
                "AA000000",
                LocalDate.ofEpochDay(1),
                null,
                passport
        );
        var constraintValidations = validator.validate(volontarioDTO);
        var constraintValidationsDataEmissione = validator.validateProperty(volontarioDTO, "dataEmissionePassaporto");
        assertFalse(constraintValidationsDataEmissione.isEmpty());
        assertEquals(constraintValidations, constraintValidationsDataEmissione);
    }

    @Test
    void VolontarioDTOFutureDataEmissioneFails() {
        var volontarioDTO = new VolontarioDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F',
                "AA000000",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(1),
                passport
        );
        var constraintValidations = validator.validate(volontarioDTO);
        var constraintValidationsDataEmissione = validator.validateProperty(volontarioDTO, "dataEmissionePassaporto");
        assertFalse(constraintValidationsDataEmissione.isEmpty());
        assertEquals(constraintValidations, constraintValidationsDataEmissione);
    }

    @Test
    void VolontarioDTOPresentDataEmissioneSucceeds() {
        var volontarioDTO = new VolontarioDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F',
                "AA000000",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(0),
                passport
        );
        var constraintValidations = validator.validate(volontarioDTO);
        assertTrue(constraintValidations.isEmpty());
    }

    @Test
    void VolontarioDTONullPassaportoFails() {
        var volontarioDTO = new VolontarioDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F',
                "AA000000",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(-1),
                null
        );
        var constraintValidations = validator.validate(volontarioDTO);
        var constraintValidationsPassaporto = validator.validateProperty(volontarioDTO, "passaporto");
        assertFalse(constraintValidationsPassaporto.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassaporto);
    }


}
