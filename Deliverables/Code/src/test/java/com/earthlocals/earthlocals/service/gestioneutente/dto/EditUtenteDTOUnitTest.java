package com.earthlocals.earthlocals.service.gestioneutente.dto;

import com.earthlocals.earthlocals.config.TestAppConfig;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EditUtenteDTO.class, TestAppConfig.class})
public class EditUtenteDTOUnitTest {
    @Autowired
    private Validator validator;

    @Test
    void EditUtenteDTOValid() {
        var editUtenteDTO = new EditUtenteDTO(
                "nome",
                "cognome",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(editUtenteDTO);
        assertTrue(constraintValidations.isEmpty());
    }

    @Test
    void EditUtenteDTONullNomeFails() {
        var editUtenteDTO = new EditUtenteDTO(
                null,
                "cognome",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(editUtenteDTO);
        var constraintValidationsNome = validator.validateProperty(editUtenteDTO, "nome");
        assertFalse(constraintValidationsNome.isEmpty());
        assertEquals(constraintValidations, constraintValidationsNome);
    }

    @Test
    void EditUtenteDTOEmptyNomeFails() {
        var editUtenteDTO = new EditUtenteDTO(
                "",
                "cognome",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(editUtenteDTO);
        var constraintValidationsNome = validator.validateProperty(editUtenteDTO, "nome");
        assertFalse(constraintValidationsNome.isEmpty());
        assertEquals(constraintValidations, constraintValidationsNome);
    }

    @Test
    void EditUtenteDTOBlankNomeFails() {
        var editUtenteDTO = new EditUtenteDTO(
                " ",
                "cognome",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(editUtenteDTO);
        var constraintValidationsNome = validator.validateProperty(editUtenteDTO, "nome");
        assertFalse(constraintValidationsNome.isEmpty());
        assertEquals(constraintValidations, constraintValidationsNome);
    }

    @Test
    void EditUtenteDTONullCognomeFails() {
        var editUtenteDTO = new EditUtenteDTO(
                "nome",
                null,
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(editUtenteDTO);
        var constraintValidationsCognome = validator.validateProperty(editUtenteDTO, "cognome");
        assertFalse(constraintValidationsCognome.isEmpty());
        assertEquals(constraintValidations, constraintValidationsCognome);
    }

    @Test
    void EditUtenteDTOEmptyCognomeFails() {
        var editUtenteDTO = new EditUtenteDTO(
                "nome",
                "",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(editUtenteDTO);
        var constraintValidationsCognome = validator.validateProperty(editUtenteDTO, "cognome");
        assertFalse(constraintValidationsCognome.isEmpty());
        assertEquals(constraintValidations, constraintValidationsCognome);
    }

    @Test
    void EditUtenteDTOBlankCognomeFails() {
        var editUtenteDTO = new EditUtenteDTO(
                "nome",
                " ",
                1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(editUtenteDTO);
        var constraintValidationsCognome = validator.validateProperty(editUtenteDTO, "cognome");
        assertFalse(constraintValidationsCognome.isEmpty());
        assertEquals(constraintValidations, constraintValidationsCognome);
    }

//    @Test
//    void EditUtenteDTONullEmailFails() {
//        var editUtenteDTO = new EditUtenteDTO(
//                "nome",
//                "cognome",
//                null,
//                1,
//                LocalDate.ofEpochDay(-1),
//                'F'
//        );
//
//        var constraintValidations = validator.validate(editUtenteDTO);
//        var constraintValidationsEmail = validator.validateProperty(editUtenteDTO, "email");
//        assertFalse(constraintValidationsEmail.isEmpty());
//        assertEquals(constraintValidations, constraintValidationsEmail);
//    }
//
//    @Test
//    void EditUtenteDTOEmptyEmailFails() {
//        var editUtenteDTO = new EditUtenteDTO(
//                "nome",
//                "cognome",
//                "",
//                1,
//                LocalDate.ofEpochDay(-1),
//                'F'
//        );
//
//        var constraintValidations = validator.validate(editUtenteDTO);
//        var constraintValidationsEmail = validator.validateProperty(editUtenteDTO, "email");
//        assertFalse(constraintValidationsEmail.isEmpty());
//        assertEquals(constraintValidations, constraintValidationsEmail);
//    }
//
//    @Test
//    void EditUtenteDTOBlankEmailFails() {
//        var editUtenteDTO = new EditUtenteDTO(
//                "nome",
//                "cognome",
//                " ",
//                1,
//                LocalDate.ofEpochDay(-1),
//                'F'
//        );
//
//        var constraintValidations = validator.validate(editUtenteDTO);
//        var constraintValidationsEmail = validator.validateProperty(editUtenteDTO, "email");
//        assertFalse(constraintValidationsEmail.isEmpty());
//        assertEquals(constraintValidations, constraintValidationsEmail);
//    }

//    @Test
//    void EditUtenteDTOMissingChiocciolaEmailFails() {
//        var editUtenteDTO = new EditUtenteDTO(
//                "nome",
//                "cognome",
//                "user.domain",
//                1,
//                LocalDate.ofEpochDay(-1),
//                'F'
//        );
//
//        var constraintValidations = validator.validate(editUtenteDTO);
//        var constraintValidationsEmail = validator.validateProperty(editUtenteDTO, "email");
//        assertFalse(constraintValidationsEmail.isEmpty());
//        assertEquals(constraintValidations, constraintValidationsEmail);
//    }

//    @Test
//    void EditUtenteDTOMissingUsernameEmailFails() {
//        var editUtenteDTO = new EditUtenteDTO(
//                "nome",
//                "cognome",
//                "@domain",
//                1,
//                LocalDate.ofEpochDay(-1),
//                'F'
//        );
//
//        var constraintValidations = validator.validate(editUtenteDTO);
//        var constraintValidationsEmail = validator.validateProperty(editUtenteDTO, "email");
//        assertFalse(constraintValidationsEmail.isEmpty());
//        assertEquals(constraintValidations, constraintValidationsEmail);
//    }

//    @Test
//    void EditUtenteDTOMissingDomainEmailFails() {
//        var editUtenteDTO = new EditUtenteDTO(
//                "nome",
//                "cognome",
//                "utente@",
//                1,
//                LocalDate.ofEpochDay(-1),
//                'F'
//        );
//
//        var constraintValidations = validator.validate(editUtenteDTO);
//        var constraintValidationsEmail = validator.validateProperty(editUtenteDTO, "email");
//        assertFalse(constraintValidationsEmail.isEmpty());
//        assertEquals(constraintValidations, constraintValidationsEmail);
//    }

    @Test
    void EditUtenteDTONullNazionalitaFails() {
        var editUtenteDTO = new EditUtenteDTO(
                "nome",
                "cognome",
                null,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(editUtenteDTO);
        var constraintValidationsNazionalita = validator.validateProperty(editUtenteDTO, "nazionalita");
        assertFalse(constraintValidationsNazionalita.isEmpty());
        assertEquals(constraintValidations, constraintValidationsNazionalita);
    }

    @Test
    void EditUtenteDTONegativeNazionalitaFails() {
        var editUtenteDTO = new EditUtenteDTO(
                "nome",
                "cognome",
                -1,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(editUtenteDTO);
        var constraintValidationsNazionalita = validator.validateProperty(editUtenteDTO, "nazionalita");
        assertFalse(constraintValidationsNazionalita.isEmpty());
        assertEquals(constraintValidations, constraintValidationsNazionalita);
    }

    @Test
    void EditUtenteDTOZeroNazionalitaSucceeds() {
        var editUtenteDTO = new EditUtenteDTO(
                "nome",
                "cognome",
                0,
                LocalDate.ofEpochDay(-1),
                'F'
        );

        var constraintValidations = validator.validate(editUtenteDTO);
        assertTrue(constraintValidations.isEmpty());
    }

    @Test
    void EditUtenteDTODataNascitaNullFails() {
        var editUtenteDTO = new EditUtenteDTO(
                "nome",
                "cognome",
                1,
                null,
                'F'
        );

        var constraintValidationsPassword = validator.validateProperty(editUtenteDTO, "dataNascita");
        var constraintValidations = validator.validate(editUtenteDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void EditUtenteDTODataNascitaTodayFails() {
        var editUtenteDTO = new EditUtenteDTO(
                "nome",
                "cognome",
                1,
                LocalDate.ofEpochDay(0),
                'F'
        );

        var constraintValidationsPassword = validator.validateProperty(editUtenteDTO, "dataNascita");
        var constraintValidations = validator.validate(editUtenteDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void EditUtenteDTODataNascitaFutureFails() {
        var editUtenteDTO = new EditUtenteDTO(
                "nome",
                "cognome",
                1,
                LocalDate.ofEpochDay(1),
                'F'
        );

        var constraintValidationsPassword = validator.validateProperty(editUtenteDTO, "dataNascita");
        var constraintValidations = validator.validate(editUtenteDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }

    @Test
    void EditUtenteDTONullSessoFails() {
        var editUtenteDTO = new EditUtenteDTO(
                "nome",
                "cognome",
                1,
                LocalDate.ofEpochDay(-1),
                null
        );

        var constraintValidations = validator.validate(editUtenteDTO);
        var constraintValidationsSesso = validator.validateProperty(editUtenteDTO, "sesso");
        assertFalse(constraintValidationsSesso.isEmpty());
        assertEquals(constraintValidations, constraintValidationsSesso);
    }

    @Test
    void EditUtenteDTOMSessoSucceeds() {
        var editUtenteDTO = new EditUtenteDTO(
                "nome",
                "cognome",
                1,
                LocalDate.ofEpochDay(-1),
                'M'
        );

        var constraintValidations = validator.validate(editUtenteDTO);
        assertTrue(constraintValidations.isEmpty());
    }

    @Test
    void EditUtenteDTOInvalidSessoFails() {
        var editUtenteDTO = new EditUtenteDTO(
                "nome",
                "cognome",
                1,
                LocalDate.ofEpochDay(-1),
                'G'
        );

        var constraintValidationsPassword = validator.validateProperty(editUtenteDTO, "sesso");
        var constraintValidations = validator.validate(editUtenteDTO);
        assertFalse(constraintValidationsPassword.isEmpty());
        assertEquals(constraintValidations, constraintValidationsPassword);
    }


}
