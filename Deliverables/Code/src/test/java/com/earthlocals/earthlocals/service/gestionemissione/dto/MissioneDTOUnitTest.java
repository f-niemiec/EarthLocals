package com.earthlocals.earthlocals.service.gestionemissione.dto;

import com.earthlocals.earthlocals.model.Utente;
import com.earthlocals.earthlocals.service.gestionemissioni.dto.MissioneDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


// TODO: Change DataJpaTest
@DataJpaTest
@ExtendWith(MockitoExtension.class)
public class MissioneDTOUnitTest {
    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        var clock = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC);
        var factory = Validation.byDefaultProvider().configure().clockProvider(() -> clock).buildValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void missioneDTOValid() {
        var utente = mock(Utente.class);
        var foto = new MockMultipartFile("file", "file".getBytes());
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "nessuna",
                null,
                foto,
                utente
        );
        var constraintValidation = validator.validate(missioneDTO);
        assertTrue(constraintValidation.isEmpty());
    }

    @Test
    void missioneDTODataInizioNullFails() {
        var utente = mock(Utente.class);
        var foto = new MockMultipartFile("file", "file".getBytes());
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "Descrizione di più di 20 caratteri",
                null,
                LocalDate.ofEpochDay(2),
                "nessuna",
                null,
                foto,
                utente
        );
        var constraintValidation = validator.validate(missioneDTO);
        var constraintValidationDataInizio = validator.validateProperty(missioneDTO, "dataInizio");
        assertFalse(constraintValidationDataInizio.isEmpty());
        assertEquals(constraintValidation, constraintValidationDataInizio);
    }

    @Test
    void missioneDTODataInizioPastFails() {
        var utente = mock(Utente.class);
        var foto = new MockMultipartFile("file", "file".getBytes());
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(-1),
                LocalDate.ofEpochDay(2),
                "nessuna",
                null,
                foto,
                utente
        );
        var constraintValidation = validator.validate(missioneDTO);
        var constraintValidationDataInizio = validator.validateProperty(missioneDTO, "dataInizio");
        assertFalse(constraintValidationDataInizio.isEmpty());
        assertEquals(constraintValidation, constraintValidationDataInizio);
    }

    @Test
    void missioneDTODataInizioPresentSucceed() {
        var utente = mock(Utente.class);
        var foto = new MockMultipartFile("file", "file".getBytes());
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(0),
                LocalDate.ofEpochDay(2),
                "nessuna",
                null,
                foto,
                utente
        );
        var constraintValidation = validator.validate(missioneDTO);
        assertTrue(constraintValidation.isEmpty());
    }

    @Test
    void missioneDTODataInizioFutureSucceed() {
        var utente = mock(Utente.class);
        var foto = new MockMultipartFile("file", "file".getBytes());
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "nessuna",
                null,
                foto,
                utente
        );
        var constraintValidation = validator.validate(missioneDTO);
        assertTrue(constraintValidation.isEmpty());
    }


}
