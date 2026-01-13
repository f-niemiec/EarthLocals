package com.earthlocals.earthlocals.service.gestioneutente;

import com.earthlocals.earthlocals.model.*;
import com.earthlocals.earthlocals.service.gestioneutente.dto.UtenteDTO;
import com.earthlocals.earthlocals.service.gestioneutente.dto.VolontarioDTO;
import com.earthlocals.earthlocals.service.gestioneutente.exceptions.UserAlreadyExistsException;
import com.earthlocals.earthlocals.service.gestioneutente.passport.PassportStorageService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        when(utenteRepository.findByEmail(any())).thenReturn(null);


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
        when(utenteRepository.findByEmail(any())).thenReturn(null);

        assertThrows(Exception.class, () -> gestioneUtente.registerVolunteer(volontarioDTO));

        verify(volontarioRepository, times(0)).save(any());
    }

    @Test
    void registerVolunteerAlreadyExistsNotPending() throws Exception {
        var volontarioDTO = mock(VolontarioDTO.class);
        var volontario = mock(Volontario.class);


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
        when(utenteRepository.findByEmail(any())).thenReturn(volontario);
        when(volontario.getPending()).thenReturn(false);

        assertThrows(UserAlreadyExistsException.class, () -> gestioneUtente.registerVolunteer(volontarioDTO));

        verify(volontarioRepository, times(0)).save(any());

    }

    @Test
    void registerVolunteerAlreadyExistsPending() throws Exception {
        var volontarioDTO = mock(VolontarioDTO.class);
        var volontario = mock(Volontario.class);
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

        when(utenteRepository.findByEmail(any())).thenReturn(volontario);
        when(volontario.getPending()).thenReturn(true);


        var res = assertDoesNotThrow(() -> gestioneUtente.registerVolunteer(volontarioDTO));

        var utenteCaptor = ArgumentCaptor.forClass(Volontario.class);
        verify(volontarioRepository, times(1)).save(utenteCaptor.capture());
        var savedUtente = utenteCaptor.getValue();

        assertSame(savedUtente, res);

    }

    @Test
    void registerOrganizer() throws Exception {
        var utenteDTO = mock(UtenteDTO.class);
        var nazioneId = 1;
        var paese = mock(Paese.class);
        var ruolo = mock(Ruolo.class);

        when(utenteDTO.getNome()).thenReturn("Nome");
        when(utenteDTO.getCognome()).thenReturn("Cognome");
        when(utenteDTO.getEmail()).thenReturn("email@example.com");
        when(utenteDTO.getPassword()).thenReturn("password");
        when(utenteDTO.getMatchingPassword()).thenReturn("matchingPassword");
        when(utenteDTO.getNazionalita()).thenReturn(nazioneId);
        when(utenteDTO.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utenteDTO.getSesso()).thenReturn('M');

        when(paeseRepository.findById(nazioneId)).thenReturn(Optional.of(paese));
        when(ruoloRepository.findByNome(Ruolo.ORGANIZER)).thenReturn(ruolo);
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(utenteRepository.save(any(Utente.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(validator.validate(utenteDTO)).thenReturn(Set.of());


        var res = assertDoesNotThrow(() -> gestioneUtente.registerOrganizer(utenteDTO));

        var utenteCaptor = ArgumentCaptor.forClass(Utente.class);
        verify(utenteRepository, times(1)).save(utenteCaptor.capture());
        var savedUtente = utenteCaptor.getValue();

        assertSame(savedUtente, res);

    }

    @Test
    void registerOrganizerConstraintValidationFails() throws Exception {
        var utenteDTO = mock(UtenteDTO.class);

        var constraintViolation = (ConstraintViolation<UtenteDTO>) mock(ConstraintViolation.class);
        when(validator.validate(utenteDTO)).thenReturn(Set.of(constraintViolation));

        assertThrows(ConstraintViolationException.class, () -> gestioneUtente.registerOrganizer(utenteDTO));

        verify(utenteRepository, times(0)).save(any());
    }

    @Test
    void registerOrganizerAlreadyExistsNotPending() throws Exception {
        var utenteDTO = mock(UtenteDTO.class);
        var utente = mock(Utente.class);


        var passport = new MockMultipartFile("File", "test.pdf", "application/pdf", new byte[0]);
        var nazioneId = 1;
        var paese = mock(Paese.class);
        var ruolo = mock(Ruolo.class);

        when(utenteDTO.getNome()).thenReturn("Nome");
        when(utenteDTO.getCognome()).thenReturn("Cognome");
        when(utenteDTO.getEmail()).thenReturn("email@example.com");
        when(utenteDTO.getPassword()).thenReturn("password");
        when(utenteDTO.getMatchingPassword()).thenReturn("matchingPassword");
        when(utenteDTO.getNazionalita()).thenReturn(nazioneId);
        when(utenteDTO.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utenteDTO.getSesso()).thenReturn('M');

        when(paeseRepository.findById(nazioneId)).thenReturn(Optional.of(paese));
        when(ruoloRepository.findByNome(Ruolo.VOLUNTEER)).thenReturn(ruolo);
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(passportStorageService.acceptUpload(passport)).thenThrow(new Exception());
        when(utenteRepository.save(any(Utente.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(validator.validate(utenteDTO)).thenReturn(Set.of());
        when(utenteRepository.findByEmail(any())).thenReturn(utente);
        when(utente.getPending()).thenReturn(false);

        assertThrows(UserAlreadyExistsException.class, () -> gestioneUtente.registerOrganizer(utenteDTO));

        verify(utenteRepository, times(0)).save(any());

    }

    @Test
    void registerOrganizerAlreadyExistsPending() throws Exception {
        var utenteDTO = mock(UtenteDTO.class);
        var utente = mock(Utente.class);
        var nazioneId = 1;
        var paese = mock(Paese.class);
        var ruolo = mock(Ruolo.class);

        when(utenteDTO.getNome()).thenReturn("Nome");
        when(utenteDTO.getCognome()).thenReturn("Cognome");
        when(utenteDTO.getEmail()).thenReturn("email@example.com");
        when(utenteDTO.getPassword()).thenReturn("password");
        when(utenteDTO.getMatchingPassword()).thenReturn("matchingPassword");
        when(utenteDTO.getNazionalita()).thenReturn(nazioneId);
        when(utenteDTO.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utenteDTO.getSesso()).thenReturn('M');

        when(paeseRepository.findById(nazioneId)).thenReturn(Optional.of(paese));
        when(ruoloRepository.findByNome(Ruolo.ORGANIZER)).thenReturn(ruolo);
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(utenteRepository.save(any(Utente.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(validator.validate(utenteDTO)).thenReturn(Set.of());
        when(utenteRepository.findByEmail(any())).thenReturn(utente);
        when(utente.getPending()).thenReturn(true);


        var res = assertDoesNotThrow(() -> gestioneUtente.registerOrganizer(utenteDTO));

        var utenteCaptor = ArgumentCaptor.forClass(Utente.class);
        verify(utenteRepository, times(1)).save(utenteCaptor.capture());
        var savedUtente = utenteCaptor.getValue();

        assertSame(savedUtente, res);

    }

    @Test
    void createVerificationToken() {
        var utente = mock(Utente.class);
        var res = gestioneUtente.createVerificationToken(utente);
        ArgumentCaptor<VerificationToken> captor = ArgumentCaptor.forClass(VerificationToken.class);
        verify(verificationTokenRepository, times(1)).save(captor.capture());
        var verToken = captor.getValue();
        assertEquals(res, verToken.getToken());
    }

    @Test
    void activateAccount() {
        String token = "abc";
        var verToken = mock(VerificationToken.class);
        var utente = mock(Utente.class);
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.of(verToken));
        when(verToken.getUtente()).thenReturn(utente);
        when(verToken.getExpiryDate()).thenReturn(LocalDateTime.now().plusDays(1));

        gestioneUtente.activateAccount(token);

        InOrder inOrder = inOrder(utente, utenteRepository);
        inOrder.verify(utente).setPending(false);
        inOrder.verify(utenteRepository).save(utente);
        verify(utente, never()).setPending(true);

        verify(verificationTokenRepository).delete(verToken);

    }

    @Test
    void activateAccountTokenNotFound() {
        String token = "abc";
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> gestioneUtente.activateAccount(token));
    }

    @Test
    void activateAccountTokenExpired() {
        String token = "abc";
        var verToken = mock(VerificationToken.class);
        var utente = mock(Utente.class);
        when(verificationTokenRepository.findByToken(token)).thenReturn(Optional.of(verToken));
        when(verToken.getUtente()).thenReturn(utente);
        when(verToken.getExpiryDate()).thenReturn(LocalDateTime.now().minusDays(1));

        assertThrows(RuntimeException.class, () -> gestioneUtente.activateAccount(token));
    }

    @Test
    void createPasswordResetToken() {
        String email = "utente@email.com";
        var utente = mock(Utente.class);
        when(utenteRepository.findByEmail(email)).thenReturn(utente);
        when(passwordResetTokenRepository.findByUtente(utente)).thenReturn(Optional.empty());

        var res = gestioneUtente.createPasswordResetToken(email);
        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository, times(1)).save(captor.capture());
        var verToken = captor.getValue();
        assertEquals(res, Optional.of(verToken.getToken()));
    }

    @Test
    void createPasswordResetTokenUtenteNull() {
        String email = "utente@email.com";
        when(utenteRepository.findByEmail(email)).thenReturn(null);

        var res = gestioneUtente.createPasswordResetToken(email);

        assertEquals(res, Optional.empty());
    }

    @Test
    void createPasswordResetTokenFindByUtentePresent() {
        String email = "utente@email.com";
        var utente = mock(Utente.class);
        var pwResetToken = mock(PasswordResetToken.class);

        when(utenteRepository.findByEmail(email)).thenReturn(utente);
        when(passwordResetTokenRepository.findByUtente(utente)).thenReturn(Optional.of(pwResetToken));

        var res = gestioneUtente.createPasswordResetToken(email);
        verify(passwordResetTokenRepository).delete(pwResetToken);
        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository, times(1)).save(captor.capture());
        var verToken = captor.getValue();
        assertEquals(res, Optional.of(verToken.getToken()));
    }


    @TestConfiguration
    @EnableMethodSecurity(prePostEnabled = true)
    static class TestConfig {

    }

}
