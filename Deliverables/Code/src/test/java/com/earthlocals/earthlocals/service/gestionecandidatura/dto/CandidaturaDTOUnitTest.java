package com.earthlocals.earthlocals.service.gestionecandidatura.dto;

import com.earthlocals.earthlocals.config.TestAppConfig;
import com.earthlocals.earthlocals.service.gestionecandidature.dto.CandidaturaDTO;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CandidaturaDTO.class, TestAppConfig.class})
public class CandidaturaDTOUnitTest {

    @Autowired
    private Validator validator;

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
