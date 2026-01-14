package com.earthlocals.earthlocals.service.gestionemissione.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;


// TODO: Change DataJpaTest
@DataJpaTest
public class MissioneDTOUnitTest {

    private static Validator validator;

    @BeforeAll
    public static void setUpValidator() {
        var factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


}
