package com.earthlocals.earthlocals.integrationtesting.service.gestioneutente;

import com.earthlocals.earthlocals.config.SystemTestAppConfig;
import com.earthlocals.earthlocals.config.TestcontainerConfig;
import com.earthlocals.earthlocals.model.*;
import com.earthlocals.earthlocals.service.gestioneutente.GestioneUtente;
import com.earthlocals.earthlocals.service.gestioneutente.dto.UtenteDTO;
import com.earthlocals.earthlocals.service.gestioneutente.dto.VolontarioDTO;
import com.earthlocals.earthlocals.service.gestioneutente.exceptions.UserAlreadyExistsException;
import com.earthlocals.earthlocals.service.gestioneutente.passport.PassportStorageService;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
@ContextConfiguration(classes = {SystemTestAppConfig.class, TestcontainerConfig.class})
@Testcontainers
@Transactional
public class GestioneUtenteBottomUpIntegrationTest {
    private final Resource passportResource = new ClassPathResource("static/resources/files/sample.pdf");
    @MockitoSpyBean
    private VolontarioRepository volontarioRepository;
    @MockitoSpyBean
    private UtenteRepository utenteRepository;
    @MockitoSpyBean
    private RuoloRepository ruoloRepository;
    @MockitoSpyBean
    private PasswordEncoder passwordEncoder;
    @MockitoSpyBean
    private PassportStorageService passportStorageService;
    @MockitoSpyBean
    private PaeseRepository paeseRepository;
    @MockitoSpyBean
    private VerificationTokenRepository verificationTokenRepository;
    @MockitoSpyBean
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @MockitoSpyBean
    private Validator validator;
    @MockitoSpyBean
    private GestioneUtente gestioneUtente;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private VolontarioDTO validVolontarioDTO() throws IOException {
        var passport = new MockMultipartFile("sample.pdf", passportResource.getInputStream());
        var volontarioDTO = new VolontarioDTO();
        var nazioneId = 1;
        volontarioDTO.setNome("Nome");
        volontarioDTO.setCognome("Cognome");
        volontarioDTO.setEmail("email@example.com");
        volontarioDTO.setPassword("PasswordMoltoSicura1234!");
        volontarioDTO.setMatchingPassword("PasswordMoltoSicura1234!");
        volontarioDTO.setNazionalita(nazioneId);
        volontarioDTO.setDataNascita(LocalDate.of(2004, Month.APRIL, 1));
        volontarioDTO.setSesso('M');
        volontarioDTO.setNumeroPassaporto("123456789");
        volontarioDTO.setDataScadenzaPassaporto(LocalDate.of(2027, Month.APRIL, 1));
        volontarioDTO.setDataEmissionePassaporto(LocalDate.of(2022, Month.APRIL, 1));
        volontarioDTO.setPassaporto(passport);
        return volontarioDTO;

    }

    private UtenteDTO validUtenteDTO() throws IOException {
        var utenteDTO = new UtenteDTO();
        var nazioneId = 1;
        utenteDTO.setNome("Nome");
        utenteDTO.setCognome("Cognome");
        utenteDTO.setEmail("email@example.com");
        utenteDTO.setPassword("PasswordMoltoSicura1234!");
        utenteDTO.setMatchingPassword("PasswordMoltoSicura1234!");
        utenteDTO.setNazionalita(nazioneId);
        utenteDTO.setDataNascita(LocalDate.of(2004, Month.APRIL, 1));
        utenteDTO.setSesso('M');
        return utenteDTO;

    }

    @Test
    void registerVolunteer() throws Exception {
        var volontarioDTO = validVolontarioDTO();


        var res = assertDoesNotThrow(() -> gestioneUtente.registerVolunteer(volontarioDTO));

        var utenteCaptor = ArgumentCaptor.forClass(Volontario.class);
        verify(volontarioRepository, times(1)).save(utenteCaptor.capture());
        var savedUtente = utenteCaptor.getValue();
        assertSame(savedUtente, res);

    }

    @Test
    void registerVolunteerConstraintValidationFails() throws Exception {
        var volontarioDTO = validVolontarioDTO();
        volontarioDTO.setNumeroPassaporto("1234567890");

        assertThrows(ConstraintViolationException.class, () -> gestioneUtente.registerVolunteer(volontarioDTO));

        verify(volontarioRepository, never()).save(any());
    }

    @Test
    void registerVolunteerPassportStorageFails() throws Exception {
        var volontarioDTO = validVolontarioDTO();
        var passport = spy(new MockMultipartFile("../../../sample.pdf", passportResource.getInputStream()));
        when(passport.getOriginalFilename()).thenReturn("../../../sample.pdf");
        volontarioDTO.setPassaporto(passport);


        var exception = assertThrows(Exception.class, () -> gestioneUtente.registerVolunteer(volontarioDTO));

        verify(volontarioRepository, never()).save(any());
    }

    @Test
    void registerVolunteerAlreadyExistsNotPending() throws Exception {
        var volontarioDTO = validVolontarioDTO();

        Paese p = paeseRepository.findById(volontarioDTO.getNazionalita()).orElseThrow();
        Ruolo ruolo = ruoloRepository.findByNome(Ruolo.VOLUNTEER);

        var utenteBuilder = Volontario.volontarioBuilder()
                .nome(volontarioDTO.getNome())
                .cognome(volontarioDTO.getCognome())
                .email(volontarioDTO.getEmail())
                .password(passwordEncoder.encode(volontarioDTO.getPassword()))
                .dataNascita(volontarioDTO.getDataNascita())
                .sesso(volontarioDTO.getSesso())
                .nazionalita(p)
                .pending(false)
                .ruoli(Collections.singletonList(ruolo))
                .numeroPassaporto(volontarioDTO.getNumeroPassaporto())
                .dataScadenzaPassaporto(volontarioDTO.getDataScadenzaPassaporto())
                .dataEmissionePassaporto(volontarioDTO.getDataEmissionePassaporto());

        volontarioRepository.save(utenteBuilder.build());

        verify(volontarioRepository, times(1)).save(any());

        assertThrows(UserAlreadyExistsException.class, () -> gestioneUtente.registerVolunteer(volontarioDTO));

        verify(volontarioRepository, times(1)).save(any());

    }

    @Test
    void registerVolunteerAlreadyExistsPending() throws Exception {
        var volontarioDTO = validVolontarioDTO();

        assertDoesNotThrow(() -> gestioneUtente.registerVolunteer(volontarioDTO));

        verify(volontarioRepository, times(1)).save(any());

        assertDoesNotThrow(() -> gestioneUtente.registerVolunteer(volontarioDTO));

        verify(volontarioRepository, times(2)).save(any());
    }

    @Test
    void registerVolunteerPaeseNotFound() throws Exception {
        var utenteDTO = validVolontarioDTO();
        var nazioneId = 400;
        utenteDTO.setNazionalita(nazioneId);

        assertThrows(Exception.class, () -> gestioneUtente.registerVolunteer(utenteDTO));
        verify(volontarioRepository, never()).save(any());
    }

    @Test
    void registerOrganizer() throws Exception {
        var utenteDTO = validUtenteDTO();


        var res = assertDoesNotThrow(() -> gestioneUtente.registerOrganizer(utenteDTO));

        var utenteCaptor = ArgumentCaptor.forClass(Utente.class);
        verify(utenteRepository, times(1)).save(utenteCaptor.capture());
        var savedUtente = utenteCaptor.getValue();
        assertSame(savedUtente, res);

    }

    @Test
    void registerOrganizerConstraintValidationFails() throws Exception {
        var utenteDTO = validUtenteDTO();
        utenteDTO.setEmail("email");

        assertThrows(ConstraintViolationException.class, () -> gestioneUtente.registerOrganizer(utenteDTO));

        verify(utenteRepository, never()).save(any());
    }

    @Test
    void registerOrganizerAlreadyExistsNotPending() throws Exception {
        var utenteDTO = validUtenteDTO();

        Paese p = paeseRepository.findById(utenteDTO.getNazionalita()).orElseThrow();
        Ruolo ruolo = ruoloRepository.findByNome(Ruolo.VOLUNTEER);

        var utenteBuilder = Utente.utenteBuilder()
                .nome(utenteDTO.getNome())
                .cognome(utenteDTO.getCognome())
                .email(utenteDTO.getEmail())
                .password(passwordEncoder.encode(utenteDTO.getPassword()))
                .dataNascita(utenteDTO.getDataNascita())
                .sesso(utenteDTO.getSesso())
                .nazionalita(p)
                .pending(false)
                .ruoli(Collections.singletonList(ruolo));

        utenteRepository.save(utenteBuilder.build());

        verify(utenteRepository, times(1)).save(any());

        assertThrows(UserAlreadyExistsException.class, () -> gestioneUtente.registerOrganizer(utenteDTO));

        verify(utenteRepository, times(1)).save(any());
    }

    @Test
    void registerOrganizerAlreadyExistsPending() throws Exception {
        var utenteDTO = validUtenteDTO();

        assertDoesNotThrow(() -> gestioneUtente.registerOrganizer(utenteDTO));

        verify(utenteRepository, times(1)).save(any());

        assertDoesNotThrow(() -> gestioneUtente.registerOrganizer(utenteDTO));

        verify(utenteRepository, times(2)).save(any());
    }

    @Test
    void registerOrganizerPaeseNotFound() throws Exception {
        var utenteDTO = validUtenteDTO();
        var nazioneId = 400;
        utenteDTO.setNazionalita(nazioneId);

        assertThrows(Exception.class, () -> gestioneUtente.registerOrganizer(utenteDTO));
        verify(utenteRepository, never()).save(any());
    }


}
