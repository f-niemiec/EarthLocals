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

@ContextConfiguration(classes = {TestAppConfig.class, EditPassportDTO.class})
@ExtendWith(SpringExtension.class)
public class EditPassportDTOUnitTest {
    private MultipartFile passport;

    @Autowired
    private Validator validator;


    @BeforeEach
    void setup() throws IOException {
        var passportResource = new ClassPathResource("static/resources/files/sample.pdf");
        passport = new MockMultipartFile("file", passportResource.getInputStream());
    }

    @Test
    void EditPassportDTOValid() {
        var editPassportDTO = new EditPassportDTO(
                "YA9200000",
                LocalDate.of(9999, 3, 23),
                LocalDate.ofEpochDay(-1),
                passport
        );

        var constraintValidations = validator.validate(editPassportDTO);
        assertTrue(constraintValidations.isEmpty());
    }

    @Test
    void EditPassportDTOPassportFails() {
        var editPassportDTO = new EditPassportDTO(
                "YA9200000000",
                LocalDate.of(9999, 3, 23),
                LocalDate.ofEpochDay(-1),
                passport
        );

        var constraintValidations = validator.validate(editPassportDTO);
        var constraintValidationsNome = validator.validateProperty(editPassportDTO, "numeroPassaporto");
        assertFalse(constraintValidations.isEmpty());
        assertEquals(constraintValidations, constraintValidationsNome);
    }

    @Test
    void EditPassportDTOFinalFails() {
        var editPassportDTO = new EditPassportDTO(
                "YA9200000",
                LocalDate.ofEpochDay(-3),
                LocalDate.ofEpochDay(-1),
                passport
        );

        var constraintValidations = validator.validate(editPassportDTO);
        var constraintValidationsNome = validator.validateProperty(editPassportDTO, "dataScadenzaPassaporto");
        assertFalse(constraintValidations.isEmpty());
        assertEquals(constraintValidations, constraintValidationsNome);
    }

    @Test
    void EditPassportDTOFutureFails() {
        var editPassportDTO = new EditPassportDTO(
                "YA9200000",
                LocalDate.of(9999, 3, 23),
                LocalDate.now(),
                passport
        );

        var constraintValidations = validator.validate(editPassportDTO);
        var constraintValidationsNome = validator.validateProperty(editPassportDTO, "dataEmissionePassaporto");
        assertFalse(constraintValidations.isEmpty());
        assertEquals(constraintValidations, constraintValidationsNome);
    }

    @Test
    void EditPassportDTOPassportNull() {
        var editPassportDTO = new EditPassportDTO(
                "YA9200000",
                LocalDate.of(9999, 3, 23),
                LocalDate.ofEpochDay(-1),
                null
        );

        var constraintValidations = validator.validate(editPassportDTO);
        var constraintValidationsNome = validator.validateProperty(editPassportDTO, "passaporto");
        assertFalse(constraintValidations.isEmpty());
        assertEquals(constraintValidations, constraintValidationsNome);
    }

    @Test
    void EditPassportDTOBlank() {
        var editPassportDTO = new EditPassportDTO(
                "",
                LocalDate.of(9999, 3, 23),
                LocalDate.ofEpochDay(-1),
                passport
        );

        var constraintValidations = validator.validate(editPassportDTO);
        var constraintValidationsNome = validator.validateProperty(editPassportDTO, "numeroPassaporto");
        assertFalse(constraintValidations.isEmpty());
        assertEquals(constraintValidations, constraintValidationsNome);
    }

    @Test
    void EditPassportDTOScadenzaNull() {
        var editPassportDTO = new EditPassportDTO(
                "YA9200000",
                null,
                LocalDate.ofEpochDay(-1),
                passport
        );

        var constraintValidations = validator.validate(editPassportDTO);
        var constraintValidationsNome = validator.validateProperty(editPassportDTO, "dataScadenzaPassaporto");
        assertFalse(constraintValidations.isEmpty());
        assertEquals(constraintValidations, constraintValidationsNome);
    }

    @Test
    void EditPassportDTONull() {
        var editPassportDTO = new EditPassportDTO(
                "YA9200000",
                LocalDate.of(9999, 3, 23),
                null,
                passport
        );

        var constraintValidations = validator.validate(editPassportDTO);
        var constraintValidationsNome = validator.validateProperty(editPassportDTO, "dataEmissionePassaporto");
        assertFalse(constraintValidations.isEmpty());
        assertEquals(constraintValidations, constraintValidationsNome);
    }

    @Test
    void EditPassportDTOPdfPassaportoSucceeds() throws IOException {
        var fileResource = new ClassPathResource("static/resources/files/sample.pdf");
        var passaporto = new MockMultipartFile("sample", fileResource.getInputStream());
        var editPassportDTO = new EditPassportDTO(
                "YA9200000",
                LocalDate.of(9999, 3, 23),
                LocalDate.ofEpochDay(-1),
                passaporto
        );

        var constraintValidations = validator.validate(editPassportDTO, EditPassportDTO.VolontarioPassport.class);
        assertTrue(constraintValidations.isEmpty());
    }

    @Test
    void EditPassportDTONonPdfPassaportoFails() throws IOException {
        var fileResource = new ClassPathResource("static/resources/files/sample.png");
        var passaporto = new MockMultipartFile("sample", fileResource.getInputStream());
        var editPassportDTO = new EditPassportDTO(
                "YA9200000",
                LocalDate.of(9999, 3, 23),
                LocalDate.ofEpochDay(-1),
                passaporto
        );

        var constraintValidations = validator.validate(editPassportDTO, EditPassportDTO.VolontarioPassport.class);
        assertFalse(constraintValidations.isEmpty());
    }


}
