package com.earthlocals.earthlocals.service.gestioneutente.dto;


import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

// TODO: Change DataJpaTest
@DataJpaTest
@ContextConfiguration(classes = EditPassportDTOUnitTest.TestConfig.class)
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class EditPassportDTOUnitTest {
    private static Validator validator;
    private MultipartFile passport;

    @BeforeAll
    public static void setUpValidator() {
        var clock = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC);
        var factory = Validation.byDefaultProvider().configure().clockProvider(() -> clock).buildValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setup() {
        passport = new MockMultipartFile("file.pdf", "file.pdf".getBytes());
    }

    @Test
    void EditPassportDTOValid(){
        var editPassportDTO = new EditPassportDTO(
                "YA9200000",
                LocalDate.of(9999, 3, 23),
                LocalDate.ofEpochDay(-1),
                passport
        );

        var constraintValidations = validator.validate(editPassportDTO);
        assertTrue(constraintValidations.isEmpty());
    }

    

    @TestConfiguration
    @EnableMethodSecurity(prePostEnabled = true)
    static class TestConfig {

    }
}
