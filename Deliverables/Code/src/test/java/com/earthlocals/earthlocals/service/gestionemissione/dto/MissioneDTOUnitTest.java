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
import org.springframework.web.multipart.MultipartFile;

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
    public void missioneDTONomeNullFails() {
        var foto = mock(MultipartFile.class);
        var creatore = mock(Utente.class);

        var missioneDTO = new MissioneDTO(
                null,
                0,
                "città",
                "la descrizione deve essere di almeno venti caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "competenze richieste",
                "requisiti extra",
                foto,
                creatore
        );
        var constraintValidations = validator.validate(missioneDTO);
        var constraintValidationsName = validator.validateProperty(missioneDTO, "nome");
        assertFalse(constraintValidationsName.isEmpty());
        assertEquals(constraintValidationsName, constraintValidations);

    }

    @Test
    public void missioneDTONomeTooShortFails() {
        var foto = mock(MultipartFile.class);
        var creatore = mock(Utente.class);

        var missioneDTO = new MissioneDTO(
                "abc",
                0,
                "città",
                "la descrizione deve essere di almeno venti caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "competenze richieste",
                "requisiti extra",
                foto,
                creatore
        );
        var constraintValidations = validator.validate(missioneDTO);
        var constraintValidationsName = validator.validateProperty(missioneDTO, "nome");
        assertFalse(constraintValidationsName.isEmpty());
        assertEquals(constraintValidationsName, constraintValidations);
    }

    @Test
    public void missioneDTONomeLongEnoughSucceeds() {
        var foto = mock(MultipartFile.class);
        var creatore = mock(Utente.class);

        var missioneDTO = new MissioneDTO(
                "abcde",
                0,
                "città",
                "la descrizione deve essere di almeno venti caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "competenze richieste",
                "requisiti extra",
                foto,
                creatore
        );
        var constraintValidations = validator.validate(missioneDTO);
        assertTrue(constraintValidations.isEmpty());
    }

    @Test
    public void missioneDTONomeTooLongFails() {
        var foto = mock(MultipartFile.class);
        var creatore = mock(Utente.class);

        var missioneDTO = new MissioneDTO(
                "ggdafgxcrufdxerjiuecyxkoiwwuamovrzcdadrsrlupseqluvzweugrqasoerspcikevdjkjfrlsttkaszonjaqsxexhalceanty",
                0,
                "città",
                "la descrizione deve essere di almeno venti caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "competenze richieste",
                "requisiti extra",
                foto,
                creatore
        );
        var constraintValidations = validator.validate(missioneDTO);
        var constraintValidationsName = validator.validateProperty(missioneDTO, "nome");
        assertFalse(constraintValidationsName.isEmpty());
        assertEquals(constraintValidationsName, constraintValidations);
    }

    @Test
    public void missioneDTONomeShortEnoughSucceeds() {
        var foto = mock(MultipartFile.class);
        var creatore = mock(Utente.class);

        var missioneDTO = new MissioneDTO(
                "ggdafgxcrufdxerjiuecyxkoiwwuamovrzcdadrsrlupseqluvzweugrqasoerspcikevdjkjfrlsttkaszonjaqsxexhalceant",
                0,
                "città",
                "la descrizione deve essere di almeno venti caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "competenze richieste",
                "requisiti extra",
                foto,
                creatore
        );
        var constraintValidations = validator.validate(missioneDTO);
        assertTrue(constraintValidations.isEmpty());
    }
}
