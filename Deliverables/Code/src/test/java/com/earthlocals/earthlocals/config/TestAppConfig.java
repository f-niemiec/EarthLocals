package com.earthlocals.earthlocals.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.apache.tika.Tika;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

@TestConfiguration
@EnableMethodSecurity(prePostEnabled = true)
public class TestAppConfig {

    @Bean
    public Validator validator(final AutowireCapableBeanFactory autowireCapableBeanFactory) {

        var clock = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC);

        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .clockProvider(() -> clock)
                .constraintValidatorFactory(new SpringConstraintValidatorFactory(autowireCapableBeanFactory))
                .buildValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        return validator;
    }

    @Bean
    public Tika tika() {
        return new Tika();
    }
}
