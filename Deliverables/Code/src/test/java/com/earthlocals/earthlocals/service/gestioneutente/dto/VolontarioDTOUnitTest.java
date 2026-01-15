package com.earthlocals.earthlocals.service.gestioneutente.dto;


import com.earthlocals.earthlocals.config.TestAppConfig;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {VolontarioDTO.class, TestAppConfig.class})
public class VolontarioDTOUnitTest {
    MultipartFile passport;
    @Autowired
    private Validator validator;

    @BeforeEach
    void setup() throws IOException {
        var passportResource = new ClassPathResource("static/resources/files/sample.pdf");
        passport = new MockMultipartFile("passport", passportResource.getInputStream());

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
    void VolontarioDTOTooLongPassportNumberFails() {
        var volontarioDTO = new VolontarioDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F',
                "AAA0000000",
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
    void VolontarioDTOWrongPatternPassportNumberFails() {
        var volontarioDTO = new VolontarioDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F',
                "a@!!",
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
    void VolontarioDTOOneCharPassportNumberSucceeds() {
        var volontarioDTO = new VolontarioDTO(
                "nome",
                "cognome",
                "utente@email.com",
                "abcYZ17!?",
                "abcYZ17!?",
                1,
                LocalDate.ofEpochDay(-1),
                'F',
                "A",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(-1),
                passport
        );
        var constraintValidations = validator.validate(volontarioDTO);
        assertTrue(constraintValidations.isEmpty());
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

    @Test
    void VolontarioDTOPdfPassaportoSucceeds() throws IOException {
        var fileResource = new ClassPathResource("static/resources/files/sample.pdf");
        var passaporto = new MockMultipartFile("sample", fileResource.getInputStream());
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
                passaporto
        );
        var constraintValidations = validator.validate(volontarioDTO, VolontarioDTO.PassaportoGroup.class);
        assertTrue(constraintValidations.isEmpty());

    }

    @Test
    void VolontarioDTONonPdfPassaportoFails() throws IOException {
        var fileResource = new ClassPathResource("static/resources/files/sample.png");
        var passaporto = new MockMultipartFile("sample", fileResource.getInputStream());
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
                passaporto
        );
        var constraintValidations = validator.validate(volontarioDTO, VolontarioDTO.PassaportoGroup.class);
        assertFalse(constraintValidations.isEmpty());

    }


}
