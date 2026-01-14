package com.earthlocals.earthlocals.service.gestionecandidatura.dto;

import com.earthlocals.earthlocals.service.gestionecandidature.dto.CandidaturaDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


// TODO: Change DataJpaTest
@DataJpaTest
public class CandidaturaDTOUnitTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        var factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    @Test
    public void candidaturaDTOValid() {
        var candidaturaDTO = new CandidaturaDTO(0L, 0L);
        var constraintValidations = validator.validate(candidaturaDTO);
        assertTrue(constraintValidations.isEmpty());
    }

    @Test
    void candidaturaDTONullCandidatoIdFails() {
        var candidaturaDTO = new CandidaturaDTO(null, 0L);
        var constraintValidations = validator.validate(candidaturaDTO);
        assertFalse(constraintValidations.isEmpty());
    }

    @Test
    void candidaturaDTONullMissioneIdFails() {
        var candidaturaDTO = new CandidaturaDTO(0L, null);
        var constraintValidations = validator.validate(candidaturaDTO);
        assertFalse(constraintValidations.isEmpty());
    }

    @Test
    void candidaturaDTONegativeCandidatoIdFails() {
        var candidaturaDTO = new CandidaturaDTO(-1L, 0L);
        var constraintValidations = validator.validate(candidaturaDTO);
        assertFalse(constraintValidations.isEmpty());
    }

    @Test
    void candidaturaDTONegativeMissioneIdFails() {
        var candidaturaDTO = new CandidaturaDTO(0L, -1L);
        var constraintValidations = validator.validate(candidaturaDTO);
        assertFalse(constraintValidations.isEmpty());
    }


}
