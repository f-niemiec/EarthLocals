package com.earthlocals.earthlocals.integration.gestioneutente;

import com.earthlocals.earthlocals.config.SystemTestAppConfig;
import com.earthlocals.earthlocals.config.TestcontainerConfig;
import com.earthlocals.earthlocals.model.*;
import com.earthlocals.earthlocals.service.gestioneutente.GestioneUtente;
import com.earthlocals.earthlocals.service.gestioneutente.dto.*;
import com.earthlocals.earthlocals.service.gestioneutente.exceptions.ExpiredVerificationTokenException;
import com.earthlocals.earthlocals.service.gestioneutente.exceptions.UserAlreadyExistsException;
import com.earthlocals.earthlocals.service.gestioneutente.exceptions.VerificationTokenNotFoundException;
import com.earthlocals.earthlocals.service.gestioneutente.exceptions.WrongPasswordException;
import com.earthlocals.earthlocals.service.gestioneutente.passport.PassportStorageService;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private Utente validUtenteEntity() throws IOException {
        var utente = new Utente();
        var nazioneId = 1;
        var paese = paeseRepository.findById(nazioneId).orElseThrow();
        utente.setNome("Nome");
        utente.setCognome("Cognome");
        utente.setEmail("email@example.com");
        utente.setPassword(passwordEncoder.encode("PasswordMoltoSicura1234!"));
        utente.setDataNascita(LocalDate.of(2004, Month.APRIL, 1));
        utente.setSesso('M');
        utente.setNazionalita(paese);
        utente.setPending(false);
        return utente;

    }

    private Volontario validVolontarioEntity() throws IOException {
        var utente = new Volontario();
        var nazioneId = 1;
        var paese = paeseRepository.findById(nazioneId).orElseThrow();
        utente.setNome("Nome");
        utente.setCognome("Cognome");
        utente.setEmail("email@example.com");
        utente.setPassword(passwordEncoder.encode("PasswordMoltoSicura1234!"));
        utente.setDataNascita(LocalDate.of(2004, Month.APRIL, 1));
        utente.setSesso('M');
        utente.setNazionalita(paese);
        utente.setPending(false);
        utente.setNumeroPassaporto("012345678");
        utente.setDataScadenzaPassaporto(LocalDate.of(2027, Month.APRIL, 1));
        utente.setDataEmissionePassaporto(LocalDate.of(2022, Month.APRIL, 1));
        utente.setPathPassaporto("passaporto.pdf");
        return utente;

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

    @Test
    void editUser() throws IOException {
        var utente = validUtenteEntity();
        utenteRepository.save(utente);
        var context = SecurityContextHolder.getContext();
        var authentication = new TestingAuthenticationToken(utente, null, "ROLE_USER");
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        clearInvocations(utenteRepository);

        var editUtenteDTO = new EditUtenteDTO();
        editUtenteDTO.setNome("NuovoNome");
        editUtenteDTO.setCognome("NuovoCognome");
        editUtenteDTO.setNazionalita(1);
        editUtenteDTO.setDataNascita(LocalDate.of(2000, Month.APRIL, 1));
        editUtenteDTO.setSesso('M');
        var res = gestioneUtente.editUser(editUtenteDTO);
        var utenteCaptor = ArgumentCaptor.forClass(Utente.class);
        verify(utenteRepository, times(1)).save(utenteCaptor.capture());
        var savedUtente = utenteCaptor.getValue();
        assertSame(savedUtente, res);

        assertEquals(editUtenteDTO.getNome(), savedUtente.getNome());
        assertEquals(editUtenteDTO.getCognome(), savedUtente.getCognome());
        assertEquals(editUtenteDTO.getDataNascita(), savedUtente.getDataNascita());
        assertEquals(editUtenteDTO.getSesso(), savedUtente.getSesso());
        assertEquals(editUtenteDTO.getNazionalita(), savedUtente.getNazionalita().getId());
    }

    @Test
    void editUserValidationFails() throws IOException {
        var utente = validUtenteEntity();
        utenteRepository.save(utente);
        var context = SecurityContextHolder.getContext();
        var authentication = new TestingAuthenticationToken(utente, null, "ROLE_USER");
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        clearInvocations(utenteRepository);

        var editUtenteDTO = new EditUtenteDTO();
        editUtenteDTO.setNome("NuovoNome");
        editUtenteDTO.setCognome("NuovoCognome");
        editUtenteDTO.setNazionalita(1);
        editUtenteDTO.setDataNascita(LocalDate.of(2077, Month.APRIL, 1));
        editUtenteDTO.setSesso('M');
        assertThrows(ConstraintViolationException.class, () -> gestioneUtente.editUser(editUtenteDTO));

        verify(utenteRepository, never()).save(any());
    }

    @Test
    @WithAnonymousUser
    void editUserAnonymousFails() throws IOException {

        var editUtenteDTO = new EditUtenteDTO();
        editUtenteDTO.setNome("NuovoNome");
        editUtenteDTO.setCognome("NuovoCognome");
        editUtenteDTO.setNazionalita(1);
        editUtenteDTO.setDataNascita(LocalDate.of(2077, Month.APRIL, 1));
        editUtenteDTO.setSesso('M');
        assertThrows(AuthorizationDeniedException.class, () -> gestioneUtente.editUser(editUtenteDTO));

        verify(utenteRepository, never()).save(any());
    }

    @Test
    void editUserPaeseNotFound() throws IOException {
        var utente = validUtenteEntity();
        utenteRepository.save(utente);
        var context = SecurityContextHolder.getContext();
        var authentication = new TestingAuthenticationToken(utente, null, "ROLE_USER");
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        clearInvocations(utenteRepository);

        var editUtenteDTO = new EditUtenteDTO();
        editUtenteDTO.setNome("NuovoNome");
        editUtenteDTO.setCognome("NuovoCognome");
        editUtenteDTO.setNazionalita(400);
        editUtenteDTO.setDataNascita(LocalDate.of(2000, Month.APRIL, 1));
        editUtenteDTO.setSesso('M');

        assertThrows(Exception.class, () -> gestioneUtente.editUser(editUtenteDTO));

        verify(utenteRepository, never()).save(any());
    }

    @Test
    void editPassword() throws IOException {
        var utente = spy(validUtenteEntity());

        utenteRepository.save(utente);
        var context = SecurityContextHolder.getContext();
        var authentication = new TestingAuthenticationToken(utente, null, "ROLE_USER");
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        clearInvocations(utenteRepository);

        var editPasswordDTO = new EditPasswordDTO();
        editPasswordDTO.setCurrentPassword("PasswordMoltoSicura1234!");
        editPasswordDTO.setNewPassword("NewPassword1234!");
        editPasswordDTO.setMatchingPassword("NewPassword1234!");
        var res = assertDoesNotThrow(() -> gestioneUtente.editPassword(editPasswordDTO));

        var passwordCaptor = ArgumentCaptor.forClass(String.class);
        var inOrder = inOrder(utente, utenteRepository);
        inOrder.verify(utente, times(1)).setPassword(passwordCaptor.capture());
        inOrder.verify(utenteRepository, times(1)).save(utente);

        assertTrue(
            passwordEncoder.matches(
                editPasswordDTO.getNewPassword(),
                passwordCaptor.getValue()
            ));

        assertEquals(utente, res);
        verify(utenteRepository, times(1)).save(any());

    }

    @Test
    @WithAnonymousUser
    void editPasswordAnonymousFails() throws IOException {
        var editPasswordDTO = new EditPasswordDTO();
        editPasswordDTO.setCurrentPassword("PasswordMoltoSicura1234!");
        editPasswordDTO.setNewPassword("NewPassword1234!");
        editPasswordDTO.setMatchingPassword("NewPassword1234!");

        assertThrows(AuthorizationDeniedException.class, () -> gestioneUtente.editPassword(editPasswordDTO));
        verify(utenteRepository, never()).save(any());
    }

    @Test
    @WithMockUser
    void editPasswordValidationFails() {
        var editPasswordDTO = new EditPasswordDTO();
        editPasswordDTO.setCurrentPassword("PasswordMoltoSicura1234!");
        editPasswordDTO.setNewPassword("NewPassword1234");
        editPasswordDTO.setMatchingPassword("NewPassword1234");

        assertThrows(ConstraintViolationException.class, () -> gestioneUtente.editPassword(editPasswordDTO));
        verify(utenteRepository, never()).save(any());
    }

    @Test
    void editPasswordNotMatchingFails() throws IOException {
        var utente = spy(validUtenteEntity());

        utenteRepository.save(utente);
        var context = SecurityContextHolder.getContext();
        var authentication = new TestingAuthenticationToken(utente, null, "ROLE_USER");
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        clearInvocations(utenteRepository);

        var editPasswordDTO = new EditPasswordDTO();
        editPasswordDTO.setCurrentPassword("PasswordMoltoSicura1234");
        editPasswordDTO.setNewPassword("NewPassword1234!");
        editPasswordDTO.setMatchingPassword("NewPassword1234!");

        assertThrows(WrongPasswordException.class, () -> gestioneUtente.editPassword(editPasswordDTO));
        verify(utenteRepository, never()).save(any());
    }

    @Test
    void editPassport() throws Exception {
        var volontario = spy(validVolontarioEntity());

        var context = SecurityContextHolder.getContext();
        var authentication = new TestingAuthenticationToken(volontario, null, "ROLE_VOLUNTEER");
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        volontarioRepository.save(volontario);
        clearInvocations(volontarioRepository);

        var passportOldPath = volontario.getPathPassaporto();
        var passport = spy(new MockMultipartFile("sample.pdf", passportResource.getInputStream()));
        var editPassportDTO = spy(new EditPassportDTO());
        editPassportDTO.setNumeroPassaporto("876543210");
        editPassportDTO.setDataScadenzaPassaporto(LocalDate.of(2027, Month.APRIL, 2));
        editPassportDTO.setDataEmissionePassaporto(LocalDate.of(2022, Month.APRIL, 2));
        editPassportDTO.setPassaporto(passport);

        var resultCaptor = new ResultCaptor<String>();
        when(passportStorageService.acceptUpload(editPassportDTO.getPassaporto())).thenAnswer(resultCaptor);

        var res = assertDoesNotThrow(() -> gestioneUtente.editPassport(editPassportDTO));

        assertEquals(res, volontario);

        verify(passportStorageService, times(1)).removeFile(passportOldPath);

        var inOrder = inOrder(volontario, volontarioRepository, passportStorageService);

        inOrder.verify(volontario, times(1)).setNumeroPassaporto(editPassportDTO.getNumeroPassaporto());
        assertEquals(editPassportDTO.getNumeroPassaporto(), volontario.getNumeroPassaporto());
        inOrder.verify(volontario, times(1)).setDataScadenzaPassaporto(editPassportDTO.getDataScadenzaPassaporto());
        assertEquals(editPassportDTO.getDataScadenzaPassaporto(), volontario.getDataScadenzaPassaporto());
        inOrder.verify(volontario, times(1)).setDataEmissionePassaporto(editPassportDTO.getDataEmissionePassaporto());
        assertEquals(editPassportDTO.getDataEmissionePassaporto(), volontario.getDataEmissionePassaporto());

        inOrder.verify(passportStorageService, times(1)).acceptUpload(editPassportDTO.getPassaporto());
        inOrder.verify(volontario, times(1)).setPathPassaporto(resultCaptor.getResult());
        assertEquals(resultCaptor.getResult(), volontario.getPathPassaporto());
        inOrder.verify(volontarioRepository, times(1)).save(volontario);

    }

    @Test
    @WithMockUser(roles = {"ORGANIZER", "MODERATOR", "ACCOUNT_MANAGER"})
    void editPassportNotVolunteerFails() throws IOException {
        var passport = spy(new MockMultipartFile("sample.pdf", passportResource.getInputStream()));
        var editPassportDTO = spy(new EditPassportDTO());
        editPassportDTO.setNumeroPassaporto("876543210");
        editPassportDTO.setDataScadenzaPassaporto(LocalDate.of(2027, Month.APRIL, 2));
        editPassportDTO.setDataEmissionePassaporto(LocalDate.of(2022, Month.APRIL, 2));
        editPassportDTO.setPassaporto(passport);
        assertThrows(AuthorizationDeniedException.class, () -> gestioneUtente.editPassport(editPassportDTO));
        verify(volontarioRepository, never()).save(any());

    }

    @Test
    @WithAnonymousUser
    void editPassportAnonymousFails() throws IOException {
        var passport = spy(new MockMultipartFile("sample.pdf", passportResource.getInputStream()));
        var editPassportDTO = spy(new EditPassportDTO());
        editPassportDTO.setNumeroPassaporto("876543210");
        editPassportDTO.setDataScadenzaPassaporto(LocalDate.of(2027, Month.APRIL, 2));
        editPassportDTO.setDataEmissionePassaporto(LocalDate.of(2022, Month.APRIL, 2));
        editPassportDTO.setPassaporto(passport);
        assertThrows(AuthorizationDeniedException.class, () -> gestioneUtente.editPassport(editPassportDTO));
        verify(volontarioRepository, never()).save(any());
    }

    @Test
    void editPassportValidationFails() throws Exception {
        var volontario = spy(validVolontarioEntity());

        var context = SecurityContextHolder.getContext();
        var authentication = new TestingAuthenticationToken(volontario, null, "ROLE_VOLUNTEER");
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        volontarioRepository.save(volontario);
        clearInvocations(volontarioRepository);

        var passport = spy(new MockMultipartFile("sample.pdf", passportResource.getInputStream()));
        var editPassportDTO = spy(new EditPassportDTO());
        editPassportDTO.setNumeroPassaporto("876543210./");
        editPassportDTO.setDataScadenzaPassaporto(LocalDate.of(2027, Month.APRIL, 2));
        editPassportDTO.setDataEmissionePassaporto(LocalDate.of(2022, Month.APRIL, 2));
        editPassportDTO.setPassaporto(passport);


        assertThrows(ConstraintViolationException.class, () -> gestioneUtente.editPassport(editPassportDTO));

        verify(volontarioRepository, never()).save(volontario);
    }

    @Test
    void editPassportUploadFails() throws Exception {
        var volontario = spy(validVolontarioEntity());

        var context = SecurityContextHolder.getContext();
        var authentication = new TestingAuthenticationToken(volontario, null, "ROLE_VOLUNTEER");
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        volontarioRepository.save(volontario);
        clearInvocations(volontarioRepository);

        var passport = spy(new MockMultipartFile("../../../../sample.pdf", passportResource.getInputStream()));
        when(passport.getOriginalFilename()).thenReturn("../../../../sample.pdf");
        var editPassportDTO = spy(new EditPassportDTO());
        editPassportDTO.setNumeroPassaporto("876543210");
        editPassportDTO.setDataScadenzaPassaporto(LocalDate.of(2027, Month.APRIL, 2));
        editPassportDTO.setDataEmissionePassaporto(LocalDate.of(2022, Month.APRIL, 2));
        editPassportDTO.setPassaporto(passport);


        assertThrows(Exception.class, () -> gestioneUtente.editPassport(editPassportDTO));

        verify(volontarioRepository, never()).save(volontario);
    }

    @Test
    void getPassportVolontarioFileResource() throws IOException {
        var volontario = validVolontarioEntity();
        var context = SecurityContextHolder.getContext();
        var authentication = new TestingAuthenticationToken(volontario, null, "ROLE_VOLUNTEER");
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        var res = assertDoesNotThrow(() -> gestioneUtente.getPassportVolontarioFileResource());
        assertEquals(res.getFilename(), volontario.getPathPassaporto());

    }

    @Test
    @WithAnonymousUser
    void getPassportVolontarioFileResourceAnonymousFails() {
        assertThrows(AuthorizationDeniedException.class, () -> gestioneUtente.getPassportVolontarioFileResource());
    }

    @Test
    @WithMockUser(roles = {"ORGANIZER", "MODERATOR", "ACCOUNT_MANAGER"})
    void getPassportVolontarioFileResourceNotVolunteerFails() {
        assertThrows(AuthorizationDeniedException.class, () -> gestioneUtente.getPassportVolontarioFileResource());
    }

    @Test
    void getPassportVolontarioFileResourceDownloadFileFails() throws IOException {
        var volontario = validVolontarioEntity();
        volontario.setPathPassaporto("../../../../downloadFileFails");
        var context = SecurityContextHolder.getContext();
        var authentication = new TestingAuthenticationToken(volontario, null, "ROLE_VOLUNTEER");
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        assertThrows(IllegalArgumentException.class, () -> gestioneUtente.getPassportVolontarioFileResource());

    }

    @Test
    void createVerificationToken() throws Exception {
        var utente = validUtenteEntity();
        utenteRepository.save(utente);

        var token = assertDoesNotThrow(() -> gestioneUtente.createVerificationToken(utente));
        ArgumentCaptor<VerificationToken> captor = ArgumentCaptor.forClass(VerificationToken.class);
        verify(verificationTokenRepository, times(1)).save(captor.capture());
        var verToken = captor.getValue();
        assertEquals(token, verToken.getToken());
        var verToken2 = assertDoesNotThrow(() -> verificationTokenRepository.findByToken(token).orElseThrow());
        assertEquals(verToken, verToken2);
    }

    @Test
    void activateAccount() throws Exception {
        var utente = spy(validUtenteEntity());
        utente.setPending(false);
        utenteRepository.save(utente);

        var verToken = spy(new VerificationToken(utente, "token"));
        verificationTokenRepository.save(verToken);
        assertDoesNotThrow(() -> gestioneUtente.activateAccount(verToken.getToken()));
        var inOrder = inOrder(utenteRepository, verificationTokenRepository, utente);

        inOrder.verify(utente, times(1)).setPending(false);
        inOrder.verify(utenteRepository, times(1)).save(utente);
        inOrder.verify(verificationTokenRepository, times(1)).delete(verToken);
        verify(utente, never()).setPending(true);

        verify(verificationTokenRepository).delete(verToken);


        assertFalse(utente.getPending());
    }

    @Test
    void activateAccountTokenNotFound() throws Exception {
        var utente = spy(validUtenteEntity());
        utente.setPending(false);
        utenteRepository.save(utente);

        assertThrows(VerificationTokenNotFoundException.class, () -> gestioneUtente.activateAccount("TOKEN"));
    }

    @Test
    void activateAccountTokenExpired() throws Exception {
        var utente = spy(validUtenteEntity());
        utente.setPending(false);
        utenteRepository.save(utente);

        var verToken = spy(new VerificationToken(utente, "token"));
        verToken.setExpiryDate(LocalDateTime.of(2020, Month.APRIL, 1, 0, 0));
        verificationTokenRepository.save(verToken);
        assertThrows(ExpiredVerificationTokenException.class, () -> gestioneUtente.activateAccount(verToken.getToken()));
    }


    @Getter
    static public class ResultCaptor<T> implements Answer<T> {
        private T result = null;

        @Override
        @SuppressWarnings("unchecked")
        public T answer(InvocationOnMock invocationOnMock) throws Throwable {
            result = (T) invocationOnMock.callRealMethod();
            return result;
        }
    }


}
