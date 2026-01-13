package com.earthlocals.earthlocals.service.gestioneutente;

import com.earthlocals.earthlocals.model.*;
import com.earthlocals.earthlocals.service.gestioneutente.dto.*;
import com.earthlocals.earthlocals.service.gestioneutente.exceptions.UserAlreadyExistsException;
import com.earthlocals.earthlocals.service.gestioneutente.exceptions.WrongPasswordException;
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
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
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

        verify(volontarioRepository, never()).save(any());
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

        verify(volontarioRepository, never()).save(any());
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

        verify(volontarioRepository, never()).save(any());

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

        verify(utenteRepository, never()).save(any());
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

        verify(utenteRepository, never()).save(any());

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
    void registerOrganizerPaeseNotFound() throws Exception {
        var utenteDTO = mock(UtenteDTO.class);
        var nazioneId = 1;
        var paese = mock(Paese.class);
        var ruolo = mock(Ruolo.class);

        when(utenteDTO.getEmail()).thenReturn("email@example.com");
        when(utenteDTO.getNazionalita()).thenReturn(nazioneId);

        when(paeseRepository.findById(nazioneId)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> gestioneUtente.registerOrganizer(utenteDTO));
        verify(utenteRepository, never()).save(any());

    }

    @Test
    void editUser() {
        var editUtenteDTO = mock(EditUtenteDTO.class);
        var context = SecurityContextHolder.getContext();
        var utente = mock(Utente.class);
        var authentication = new TestingAuthenticationToken(utente, null, "ROLE_VOLUNTEER");
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        var paese = mock(Paese.class);
        var paeseId = 1;
        when(editUtenteDTO.getNome()).thenReturn("Nome");
        when(editUtenteDTO.getCognome()).thenReturn("Cognome");
        when(editUtenteDTO.getEmail()).thenReturn("email@example.com");
        when(editUtenteDTO.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(editUtenteDTO.getSesso()).thenReturn('M');
        when(editUtenteDTO.getNazionalita()).thenReturn(paeseId);

        when(paeseRepository.findById(paeseId)).thenReturn(Optional.of(paese));
        when(utenteRepository.save(any(Utente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var res = gestioneUtente.editUser(editUtenteDTO);
        var utenteCaptor = ArgumentCaptor.forClass(Utente.class);
        verify(utenteRepository, times(1)).save(utenteCaptor.capture());
        var savedUtente = utenteCaptor.getValue();
        assertSame(savedUtente, res);

        var nomeCaptor = ArgumentCaptor.forClass(String.class);
        verify(utente, times(1)).setNome(nomeCaptor.capture());
        assertEquals(editUtenteDTO.getNome(), nomeCaptor.getValue());

        var cognomeCaptor = ArgumentCaptor.forClass(String.class);
        verify(utente, times(1)).setCognome(cognomeCaptor.capture());
        assertEquals(editUtenteDTO.getCognome(), cognomeCaptor.getValue());

        var emailCaptor = ArgumentCaptor.forClass(String.class);
        verify(utente, times(1)).setEmail(emailCaptor.capture());
        assertEquals(editUtenteDTO.getEmail(), emailCaptor.getValue());

        var dataNascitaCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(utente, times(1)).setDataNascita(dataNascitaCaptor.capture());
        assertEquals(editUtenteDTO.getDataNascita(), dataNascitaCaptor.getValue());

        var sessoCaptor = ArgumentCaptor.forClass(Character.class);
        verify(utente, times(1)).setSesso(sessoCaptor.capture());
        assertEquals(editUtenteDTO.getSesso(), sessoCaptor.getValue());

        var nazionalitaCaptor = ArgumentCaptor.forClass(Paese.class);
        verify(utente, times(1)).setNazionalita(nazionalitaCaptor.capture());
        assertEquals(paese, nazionalitaCaptor.getValue());
    }

    @Test
    void editUserValidationFails() {
        var editUtenteDTO = mock(EditUtenteDTO.class);
        var constraintViolation = (ConstraintViolation<EditUtenteDTO>) mock(ConstraintViolation.class);
        when(validator.validate(editUtenteDTO)).thenReturn(Set.of(constraintViolation));

        assertThrows(ConstraintViolationException.class, () -> gestioneUtente.editUser(editUtenteDTO));

        verify(utenteRepository, never()).save(any());
    }

    // TODO: Test for unauthenticated user in editUser when Method Security will work

    @Test
    void editUserPaeseNotFound() {
        var editUtenteDTO = mock(EditUtenteDTO.class);
        var context = SecurityContextHolder.getContext();
        var utente = mock(Utente.class);
        var authentication = new TestingAuthenticationToken(utente, null, "ROLE_VOLUNTEER");
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        var paeseId = 1;

        when(paeseRepository.findById(paeseId)).thenReturn(Optional.empty());
        when(editUtenteDTO.getNazionalita()).thenReturn(paeseId);

        assertThrows(Exception.class, () -> gestioneUtente.editUser(editUtenteDTO));

        verify(utenteRepository, never()).save(any());
    }

    @Test
    void editPassword() {
        var editPasswordDTO = mock(EditPasswordDTO.class);
        var encodedPassword = "encodedNewPassword";
        var currentPassword = "currentPassword";
        var context = SecurityContextHolder.getContext();
        var utente = mock(Utente.class);
        var authentication = new TestingAuthenticationToken(utente, null, "ROLE_VOLUNTEER");
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        when(editPasswordDTO.getNewPassword()).thenReturn("newPassword");
        when(editPasswordDTO.getCurrentPassword()).thenReturn(currentPassword);
        when(utente.getPassword()).thenReturn(currentPassword);

        when(passwordEncoder.matches(editPasswordDTO.getCurrentPassword(), utente.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(editPasswordDTO.getNewPassword())).thenReturn(encodedPassword);
        when(utenteRepository.save(utente)).thenAnswer(invocation -> invocation.getArgument(0));

        var res = assertDoesNotThrow(() -> gestioneUtente.editPassword(editPasswordDTO));

        var passwordCaptor = ArgumentCaptor.forClass(String.class);
        var inOrder = inOrder(utente, utenteRepository);
        inOrder.verify(utente, times(1)).setPassword(passwordCaptor.capture());
        inOrder.verify(utenteRepository, times(1)).save(utente);
        assertEquals(encodedPassword, passwordCaptor.getValue());

        assertEquals(res, utente);
    }

    // TODO: Test for unauthenticated user in editPassword when Method Security will work

    @Test
    void editPasswordValidationFails() {
        var editPasswordDTO = mock(EditPasswordDTO.class);

        var context = SecurityContextHolder.getContext();
        var utente = mock(Utente.class);
        var authentication = new TestingAuthenticationToken(utente, null, "ROLE_VOLUNTEER");
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        var constraintViolation = (ConstraintViolation<EditPasswordDTO>) mock(ConstraintViolation.class);
        when(validator.validate(editPasswordDTO)).thenReturn(Set.of(constraintViolation));


        assertThrows(ConstraintViolationException.class, () -> gestioneUtente.editPassword(editPasswordDTO));
        verify(utenteRepository, never()).save(any());

    }

    @Test
    void editPasswordNotMatching() {
        var editPasswordDTO = mock(EditPasswordDTO.class);
        var currentPassword = "currentPassword";
        var context = SecurityContextHolder.getContext();
        var utente = mock(Utente.class);
        var authentication = new TestingAuthenticationToken(utente, null, "ROLE_VOLUNTEER");
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        when(editPasswordDTO.getNewPassword()).thenReturn("newPassword");
        when(editPasswordDTO.getCurrentPassword()).thenReturn(currentPassword);
        when(utente.getPassword()).thenReturn(currentPassword);

        when(passwordEncoder.matches(editPasswordDTO.getCurrentPassword(), utente.getPassword())).thenReturn(false);

        assertThrows(WrongPasswordException.class, () -> gestioneUtente.editPassword(editPasswordDTO));

        verify(utenteRepository, never()).save(any());

    }

    @Test
    void editPassport() throws Exception {

        var numeroPassaporto = "012345678";
        var scadenzaPassaporto = LocalDate.now().plusDays(1);
        var emissionePassaporto = LocalDate.now().minusDays(1);
        var mockFile = new MockMultipartFile("file", "file".getBytes());
        var oldPath = "oldPath";
        var newPath = "newPath";

        var editPassportDTO = mock(EditPassportDTO.class);

        var context = SecurityContextHolder.getContext();
        var volontario = mock(Volontario.class);
        var authentication = new TestingAuthenticationToken(volontario, null, "ROLE_VOLUNTEER");
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        when(editPassportDTO.getNumeroPassaporto()).thenReturn(numeroPassaporto);
        when(editPassportDTO.getDataScadenzaPassaporto()).thenReturn(scadenzaPassaporto);
        when(editPassportDTO.getDataEmissionePassaporto()).thenReturn(emissionePassaporto);
        when(editPassportDTO.getPassaporto()).thenReturn(mockFile);
        when(volontario.getPathPassaporto()).thenReturn(oldPath);
        when(passportStorageService.acceptUpload(mockFile)).thenReturn(newPath);
        when(volontarioRepository.save(any())).thenAnswer(a -> a.getArgument(0));

        var res = assertDoesNotThrow(() -> gestioneUtente.editPassport(editPassportDTO));
        assertEquals(res, volontario);

        verify(passportStorageService, times(1))
                .removeFile(oldPath);

        var inOrder = inOrder(volontario, volontarioRepository, passportStorageService);

        var numeroPassaportoCaptor = ArgumentCaptor.forClass(String.class);
        inOrder
                .verify(volontario, times(1))
                .setNumeroPassaporto(numeroPassaportoCaptor.capture());
        assertEquals(numeroPassaporto, numeroPassaportoCaptor.getValue());

        var dataScadenzaPassaportoCaptor = ArgumentCaptor.forClass(LocalDate.class);
        inOrder
                .verify(volontario, times(1))
                .setDataScadenzaPassaporto(dataScadenzaPassaportoCaptor.capture());
        assertEquals(scadenzaPassaporto, dataScadenzaPassaportoCaptor.getValue());

        var dataEmissionePassaportoCaptor = ArgumentCaptor.forClass(LocalDate.class);
        inOrder
                .verify(volontario, times(1))
                .setDataEmissionePassaporto(dataEmissionePassaportoCaptor.capture());
        assertEquals(emissionePassaporto, dataEmissionePassaportoCaptor.getValue());


        inOrder.verify(passportStorageService, times(1))
                .acceptUpload(mockFile);

        inOrder.verify(volontario, times(1))
                .setPathPassaporto(newPath);

        inOrder.verify(volontarioRepository, times(1)).save(volontario);


    }

    @Test
    void editPassportValidationFails() throws Exception {

        var context = SecurityContextHolder.getContext();
        var volontario = mock(Volontario.class);
        var authentication = new TestingAuthenticationToken(volontario, null, "ROLE_VOLUNTEER");
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        var editPassportDTO = mock(EditPassportDTO.class);

        var constraintViolation = (ConstraintViolation<EditPassportDTO>) mock(ConstraintViolation.class);
        when(validator.validate(editPassportDTO)).thenReturn(Set.of(constraintViolation));
        assertThrows(ConstraintViolationException.class, () -> gestioneUtente.editPassport(editPassportDTO));

        verify(volontarioRepository, never()).save(any());
    }

    @Test
    void editPassportUploadFails() throws Exception {

        var numeroPassaporto = "012345678";
        var scadenzaPassaporto = LocalDate.now().plusDays(1);
        var emissionePassaporto = LocalDate.now().minusDays(1);
        var mockFile = new MockMultipartFile("file", "file".getBytes());
        var oldPath = "oldPath";
        var newPath = "newPath";

        var editPassportDTO = mock(EditPassportDTO.class);

        var context = SecurityContextHolder.getContext();
        var volontario = mock(Volontario.class);
        var authentication = new TestingAuthenticationToken(volontario, null, "ROLE_VOLUNTEER");
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        when(editPassportDTO.getNumeroPassaporto()).thenReturn(numeroPassaporto);
        when(editPassportDTO.getDataScadenzaPassaporto()).thenReturn(scadenzaPassaporto);
        when(editPassportDTO.getDataEmissionePassaporto()).thenReturn(emissionePassaporto);
        when(editPassportDTO.getPassaporto()).thenReturn(mockFile);
        when(volontario.getPathPassaporto()).thenReturn(oldPath);
        when(passportStorageService.acceptUpload(mockFile)).thenThrow(Exception.class);
        when(volontarioRepository.save(any())).thenAnswer(a -> a.getArgument(0));

        var res = assertThrows(Exception.class, () -> gestioneUtente.editPassport(editPassportDTO));

        verify(volontarioRepository, never()).save(any());
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
