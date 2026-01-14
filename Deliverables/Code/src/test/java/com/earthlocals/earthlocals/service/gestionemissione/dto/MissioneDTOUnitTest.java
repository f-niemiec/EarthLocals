package com.earthlocals.earthlocals.service.gestionemissione.dto;

import com.earthlocals.earthlocals.model.Utente;
import com.earthlocals.earthlocals.service.gestionemissioni.dto.MissioneDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

// TODO: Change DataJpaTest
@ExtendWith(SpringExtension.class)
public class MissioneDTOUnitTest {
    private static Validator validator;

    private MultipartFile foto;

    @BeforeAll
    public static void setUpValidator() {
        var clock = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC);
        var factory = Validation.byDefaultProvider().configure().clockProvider(() -> clock).buildValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setup() {
        foto = new MockMultipartFile("file.png", "file.png".getBytes());
    }

    @Test
    void missioneDTOValid() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "nessuna",
                "nessuno",
                foto,
                utente
        );
        var constraintValidation = validator.validate(missioneDTO);
        assertTrue(constraintValidation.isEmpty());
    }

    @Test
    void missioneDTONomeNullFails() {
        var utente = mock(Utente.class);

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
                utente
        );
        var constraintValidations = validator.validate(missioneDTO);
        var constraintValidationsName = validator.validateProperty(missioneDTO, "nome");
        assertFalse(constraintValidationsName.isEmpty());
        assertEquals(constraintValidationsName, constraintValidations);

    }

    @Test
    void missioneDTONomeBlankFails() {
        var utente = mock(Utente.class);

        var missioneDTO = new MissioneDTO(
                "     ",
                0,
                "città",
                "la descrizione deve essere di almeno venti caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "competenze richieste",
                "requisiti extra",
                foto,
                utente
        );
        var constraintValidations = validator.validate(missioneDTO);
        var constraintValidationsName = validator.validateProperty(missioneDTO, "nome");
        assertFalse(constraintValidationsName.isEmpty());
        assertEquals(constraintValidationsName, constraintValidations);
    }

    @Test
    void missioneDTONomeTooShortFails() {
        var utente = mock(Utente.class);

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
                utente
        );
        var constraintValidations = validator.validate(missioneDTO);
        var constraintValidationsName = validator.validateProperty(missioneDTO, "nome");
        assertFalse(constraintValidationsName.isEmpty());
        assertEquals(constraintValidationsName, constraintValidations);
    }

    @Test
    void missioneDTONomeLongEnoughSucceeds() {
        var utente = mock(Utente.class);

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
                utente
        );
        var constraintValidations = validator.validate(missioneDTO);
        assertTrue(constraintValidations.isEmpty());
    }

    @Test
    void missioneDTONomeTooLongFails() {
        var utente = mock(Utente.class);

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
                utente
        );
        var constraintValidations = validator.validate(missioneDTO);
        var constraintValidationsName = validator.validateProperty(missioneDTO, "nome");
        assertFalse(constraintValidationsName.isEmpty());
        assertEquals(constraintValidationsName, constraintValidations);
    }

    @Test
    void missioneDTONomeShortEnoughSucceeds() {
        var utente = mock(Utente.class);

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
                utente
        );
        var constraintValidations = validator.validate(missioneDTO);
        assertTrue(constraintValidations.isEmpty());
    }

    @Test
    void missioneDTONegativePaeseFails() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                -1,
                "Salerno",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "nessuna",
                "nessuno",
                foto,
                utente
        );
        var constraintValidations = validator.validate(missioneDTO);
        var constraintValidationsPaese = validator.validateProperty(missioneDTO, "paese");
        assertFalse(constraintValidationsPaese.isEmpty());
        assertEquals(constraintValidationsPaese, constraintValidations);
    }

    @Test
    void missioneDTONullPaeseFails() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                null,
                "Salerno",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "nessuna",
                "nessuno",
                foto,
                utente
        );
        var constraintValidations = validator.validate(missioneDTO);
        var constraintValidationsPaese = validator.validateProperty(missioneDTO, "paese");
        assertFalse(constraintValidationsPaese.isEmpty());
        assertEquals(constraintValidationsPaese, constraintValidations);
    }

    @Test
    void missioneDTOPositivePaeseSucceeds() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                1,
                "Salerno",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "nessuna",
                "nessuno",
                foto,
                utente
        );
        var constraintValidations = validator.validate(missioneDTO);
        assertTrue(constraintValidations.isEmpty());
    }

    @Test
    void missioneDTONullCittaFails() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                null,
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "nessuna",
                "nessuno",
                foto,
                utente
        );
        var constraintValidations = validator.validate(missioneDTO);
        var constraintValidationsCitta = validator.validateProperty(missioneDTO, "citta");
        assertFalse(constraintValidationsCitta.isEmpty());
        assertEquals(constraintValidationsCitta, constraintValidations);
    }

    @Test
    void missioneDTOBlankCittaFails() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "    ",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "nessuna",
                "nessuno",
                foto,
                utente
        );
        var constraintValidations = validator.validate(missioneDTO);
        var constraintValidationsCitta = validator.validateProperty(missioneDTO, "citta");
        assertFalse(constraintValidationsCitta.isEmpty());
        assertEquals(constraintValidationsCitta, constraintValidations);
    }


    @Test
    void missioneDTOEmptyCittaFails() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "nessuna",
                "nessuno",
                foto,
                utente
        );
        var constraintValidations = validator.validate(missioneDTO);
        var constraintValidationsCitta = validator.validateProperty(missioneDTO, "citta");
        assertFalse(constraintValidationsCitta.isEmpty());
        assertEquals(constraintValidationsCitta, constraintValidations);
    }

    @Test
    void missioneDTONotBlankCittaSucceeds() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "a",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "nessuna",
                "nessuno",
                foto,
                utente
        );
        var constraintValidations = validator.validate(missioneDTO);
        assertTrue(constraintValidations.isEmpty());
    }

    @Test
    void missioneDTONullDescrizioneFails() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                null,
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "nessuna",
                "nessuno",
                foto,
                utente
        );
        var constraintValidations = validator.validate(missioneDTO);
        var constraintValidationsDescrizione = validator.validateProperty(missioneDTO, "descrizione");
        assertFalse(constraintValidationsDescrizione.isEmpty());
        assertEquals(constraintValidationsDescrizione, constraintValidations);
    }

    @Test
    void missioneDTOBlankDescrizioneFails() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "    ",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "nessuna",
                "nessuno",
                foto,
                utente
        );
        var constraintValidations = validator.validate(missioneDTO);
        var constraintValidationsDescrizione = validator.validateProperty(missioneDTO, "descrizione");
        assertFalse(constraintValidationsDescrizione.isEmpty());
        assertEquals(constraintValidationsDescrizione, constraintValidations);
    }

    @Test
    void missioneDTOEmptyDescrizioneFails() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "nessuna",
                "nessuno",
                foto,
                utente
        );
        var constraintValidations = validator.validate(missioneDTO);
        var constraintValidationsDescrizione = validator.validateProperty(missioneDTO, "descrizione");
        assertFalse(constraintValidationsDescrizione.isEmpty());
        assertEquals(constraintValidationsDescrizione, constraintValidations);
    }

    @Test
    void missioneDTODescrizioneTooShortFails() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "abc",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "nessuna",
                "nessuno",
                foto,
                utente
        );
        var constraintValidations = validator.validate(missioneDTO);
        var constraintValidationsDescrizione = validator.validateProperty(missioneDTO, "descrizione");
        assertFalse(constraintValidationsDescrizione.isEmpty());
        assertEquals(constraintValidationsDescrizione, constraintValidations);
    }

    @Test
    void missioneDTODescrizioneLongEnoughSucceeds() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "Solo venti caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "nessuna",
                "nessuno",
                foto,
                utente
        );
        var constraintValidations = validator.validate(missioneDTO);
        assertTrue(constraintValidations.isEmpty());
    }

    @Test
    void missioneDTODataInizioNullFails() {
        var utente = mock(Utente.class);
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
    void missioneDTODataFineNullFails() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(2),
                null,
                "nessuna",
                null,
                foto,
                utente
        );
        var constraintValidationDataFine = validator.validateProperty(missioneDTO, "dataFine");
        assertFalse(constraintValidationDataFine.isEmpty());
        var constraintValidation = validator.validate(missioneDTO);
        assertEquals(constraintValidation, constraintValidationDataFine);
    }

    @Test
    void missioneDTODataFinePastFails() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(-2),
                LocalDate.ofEpochDay(-1),
                "nessuna",
                null,
                foto,
                utente
        );
        var constraintValidationDataFine = validator.validateProperty(missioneDTO, "dataFine");
        assertFalse(constraintValidationDataFine.isEmpty());
        var constraintValidationDataInizio = validator.validateProperty(missioneDTO, "dataInizio");
        assertFalse(constraintValidationDataInizio.isEmpty());
        var unionSet = new HashSet<>(constraintValidationDataInizio);
        unionSet.addAll(constraintValidationDataFine);

        var constraintValidation = validator.validate(missioneDTO);
        assertEquals(constraintValidation, unionSet);
    }

    @Test
    void missioneDTODataFinePresentFails() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(0),
                LocalDate.ofEpochDay(0),
                "nessuna",
                null,
                foto,
                utente
        );
        var constraintValidationDataFine = validator.validateProperty(missioneDTO, "dataFine");
        assertFalse(constraintValidationDataFine.isEmpty());
        var constraintValidation = validator.validate(missioneDTO);
        assertEquals(constraintValidation, constraintValidationDataFine);
    }

    @Test
    void missioneDTODataFineFutureSucceed() {
        var utente = mock(Utente.class);
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
    void missioneDTODateOverlapFails() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(2),
                LocalDate.ofEpochDay(1),
                "nessuna",
                null,
                foto,
                utente
        );
        var constraintValidationDataOverlap = validator.validate(missioneDTO, MissioneDTO.MissioneDatesOverlap.class);
        assertFalse(constraintValidationDataOverlap.isEmpty());
        var constraintValidation = validator.validate(missioneDTO);
        assertEquals(constraintValidation, constraintValidationDataOverlap);
    }

    @Test
    void missioneDTODateSameDaySucceed() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(1),
                "nessuna",
                null,
                foto,
                utente
        );
        var constraintValidation = validator.validate(missioneDTO);
        assertTrue(constraintValidation.isEmpty());
    }

    @Test
    void missioneDTONullCompetenzeRichiesteFails() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                null,
                "nessuno",
                foto,
                utente
        );
        var constraintValidation = validator.validate(missioneDTO);
        var constraintValidationCompetenze = validator.validateProperty(missioneDTO, "competenzeRichieste");
        assertFalse(constraintValidationCompetenze.isEmpty());
        assertEquals(constraintValidation, constraintValidationCompetenze);
    }

    @Test
    void missioneDTOEmptyCompetenzeRichiesteFails() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "",
                "nessuno",
                foto,
                utente
        );
        var constraintValidation = validator.validate(missioneDTO);
        var constraintValidationCompetenze = validator.validateProperty(missioneDTO, "competenzeRichieste");
        assertFalse(constraintValidationCompetenze.isEmpty());
        assertEquals(constraintValidation, constraintValidationCompetenze);
    }

    @Test
    void missioneDTONotBlankCompetenzeRichiesteSucceeds() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "a",
                "nessuno",
                foto,
                utente
        );
        var constraintValidation = validator.validate(missioneDTO);
        assertTrue(constraintValidation.isEmpty());
    }

    @Test
    void missioneDTOBlankCompetenzeRichiesteFails() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "    ",
                "nessuno",
                foto,
                utente
        );
        var constraintValidation = validator.validate(missioneDTO);
        var constraintValidationCompetenze = validator.validateProperty(missioneDTO, "competenzeRichieste");
        assertFalse(constraintValidationCompetenze.isEmpty());
        assertEquals(constraintValidation, constraintValidationCompetenze);
    }

    @Test
    void missioneDTONullRequisitiExtraSucceeds() {
        var utente = mock(Utente.class);
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
    void missioneDTOEmptyRequisitiExtraSucceeds() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "nessuna",
                "",
                foto,
                utente
        );
        var constraintValidation = validator.validate(missioneDTO);
        assertTrue(constraintValidation.isEmpty());
    }

    @Test
    void missioneDTONotBlankRequisitiExtraSucceeds() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "nessuna",
                "requisitiExtra",
                foto,
                utente
        );
        var constraintValidation = validator.validate(missioneDTO);
        assertTrue(constraintValidation.isEmpty());
    }

    @Test
    void missioneDTONullFotoFails() {
        var utente = mock(Utente.class);
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "nessuna",
                "nessuno",
                null,
                utente
        );
        var constraintValidation = validator.validate(missioneDTO);
        var constraintValidationFoto = validator.validateProperty(missioneDTO, "foto");
        assertFalse(constraintValidationFoto.isEmpty());
        assertEquals(constraintValidation, constraintValidationFoto);
    }

    @Test
    void missioneDTONullCreatoreSucceeds() {
        var missioneDTO = new MissioneDTO(
                "Help teaching a Pechino",
                0,
                "Salerno",
                "Descrizione di più di 20 caratteri",
                LocalDate.ofEpochDay(1),
                LocalDate.ofEpochDay(2),
                "nessuna",
                "nessuno",
                foto,
                null
        );
        var constraintValidation = validator.validate(missioneDTO);
        assertTrue(constraintValidation.isEmpty());
    }
}
