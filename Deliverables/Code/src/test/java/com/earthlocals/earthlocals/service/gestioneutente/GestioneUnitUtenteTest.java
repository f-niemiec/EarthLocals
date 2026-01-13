package com.earthlocals.earthlocals.service.gestioneutente;

import com.earthlocals.earthlocals.model.*;
import com.earthlocals.earthlocals.service.gestioneutente.dto.VolontarioDTO;
import com.earthlocals.earthlocals.service.gestioneutente.passport.PassportStorageService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class GestioneUnitUtenteTest {
    @Mock
    private VolontarioRepository volontarioRepository;
    @Mock
    private UtenteRepository utenteRepository;
    @Mock
    private RuoloRepository ruoloRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PassportStorageService passportStorageService;
    @Mock
    private PaeseRepository paeseRepository;
    @Mock
    private VerificationTokenRepository verificationTokenRepository;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private Validator validator;
    @InjectMocks
    private GestioneUtente gestioneUtente;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerVolunteer() throws Exception {
        var volontarioDTO = mock(VolontarioDTO.class);
        var passport = new MockMultipartFile("File", "test.pdf", "application/pdf", new byte[0]);
        var nazioneId = 1;
        var paese = mock(Paese.class);
        var ruolo = mock(Ruolo.class);

        when(volontarioDTO.getNome()).thenReturn("Nome");
        when(volontarioDTO.getCognome()).thenReturn("Cognome");
        when(volontarioDTO.getEmail()).thenReturn("email@example.com");
        when(volontarioDTO.getPassword()).thenReturn("password");
        when(volontarioDTO.getMatchingPassword()).thenReturn("matchingPassword");
        when(volontarioDTO.getNazionalita()).thenReturn(nazioneId);
        when(volontarioDTO.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(volontarioDTO.getSesso()).thenReturn('M');
        when(volontarioDTO.getNumeroPassaporto()).thenReturn("1234567890");
        when(volontarioDTO.getDataScadenzaPassaporto()).thenReturn(LocalDate.of(2027, Month.APRIL, 1));
        when(volontarioDTO.getDataEmissionePassaporto()).thenReturn(LocalDate.of(2022, Month.APRIL, 1));
        when(volontarioDTO.getPassaporto()).thenReturn(passport);

        when(paeseRepository.findById(nazioneId)).thenReturn(Optional.of(paese));
        when(ruoloRepository.findByNome(Ruolo.VOLUNTEER)).thenReturn(ruolo);
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(passportStorageService.acceptUpload(passport)).thenReturn("test.pdf");
        when(volontarioRepository.save(any(Volontario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(validator.validate(volontarioDTO)).thenReturn(Set.of());


        var res = assertDoesNotThrow(() -> gestioneUtente.registerVolunteer(volontarioDTO));

        var utenteCaptor = ArgumentCaptor.forClass(Volontario.class);
        verify(volontarioRepository, times(1)).save(utenteCaptor.capture());
        var savedUtente = utenteCaptor.getValue();

        assertSame(savedUtente, res);

    }

    @Test
    void registerVolunteerConstraintValidationFails() throws Exception {
        var volontarioDTO = mock(VolontarioDTO.class);

        var constraintViolation = (ConstraintViolation<VolontarioDTO>) mock(ConstraintViolation.class);
        when(validator.validate(volontarioDTO)).thenReturn(Set.of(constraintViolation));

        assertThrows(ConstraintViolationException.class, () -> gestioneUtente.registerVolunteer(volontarioDTO));

        verify(volontarioRepository, times(0)).save(any());
    }

    @Test
    void registerVolunteerPassportStorageFails() throws Exception {
        var volontarioDTO = mock(VolontarioDTO.class);
        var passport = new MockMultipartFile("File", "test.pdf", "application/pdf", new byte[0]);
        var nazioneId = 1;
        var paese = mock(Paese.class);
        var ruolo = mock(Ruolo.class);

        when(volontarioDTO.getNome()).thenReturn("Nome");
        when(volontarioDTO.getCognome()).thenReturn("Cognome");
        when(volontarioDTO.getEmail()).thenReturn("email@example.com");
        when(volontarioDTO.getPassword()).thenReturn("password");
        when(volontarioDTO.getMatchingPassword()).thenReturn("matchingPassword");
        when(volontarioDTO.getNazionalita()).thenReturn(nazioneId);
        when(volontarioDTO.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(volontarioDTO.getSesso()).thenReturn('M');
        when(volontarioDTO.getNumeroPassaporto()).thenReturn("1234567890");
        when(volontarioDTO.getDataScadenzaPassaporto()).thenReturn(LocalDate.of(2027, Month.APRIL, 1));
        when(volontarioDTO.getDataEmissionePassaporto()).thenReturn(LocalDate.of(2022, Month.APRIL, 1));
        when(volontarioDTO.getPassaporto()).thenReturn(passport);

        when(paeseRepository.findById(nazioneId)).thenReturn(Optional.of(paese));
        when(ruoloRepository.findByNome(Ruolo.VOLUNTEER)).thenReturn(ruolo);
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(passportStorageService.acceptUpload(passport)).thenThrow(new Exception());
        when(volontarioRepository.save(any(Volontario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(validator.validate(volontarioDTO)).thenReturn(Set.of());

        assertThrows(Exception.class, () -> gestioneUtente.registerVolunteer(volontarioDTO));

        verify(volontarioRepository, times(0)).save(any());
    }


    @TestConfiguration
    @EnableMethodSecurity(prePostEnabled = true)
    static class TestConfig {

    }

}
