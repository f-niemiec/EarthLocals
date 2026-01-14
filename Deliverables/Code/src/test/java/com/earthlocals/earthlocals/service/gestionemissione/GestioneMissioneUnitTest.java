package com.earthlocals.earthlocals.service.gestionemissione;

import com.earthlocals.earthlocals.config.TestAppConfig;
import com.earthlocals.earthlocals.model.*;
import com.earthlocals.earthlocals.service.gestionemissioni.GestioneMissione;
import com.earthlocals.earthlocals.service.gestionemissioni.dto.MissioneDTO;
import com.earthlocals.earthlocals.service.gestionemissioni.exceptions.MissioneNotAcceptableException;
import com.earthlocals.earthlocals.service.gestionemissioni.exceptions.MissioneNotFoundException;
import com.earthlocals.earthlocals.service.gestionemissioni.pictures.PicturesFilesystemStorage;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {
        TestAppConfig.class,
        GestioneMissione.class
})
@ExtendWith(SpringExtension.class)
public class GestioneMissioneUnitTest {
    @MockitoBean
    private Validator validator;
    @MockitoBean
    private MissioneRepository missioneRepository;
    @MockitoBean
    private PicturesFilesystemStorage storageService;
    @MockitoBean
    private PaeseRepository paeseRepository;
    @Autowired
    private GestioneMissione gestioneMissione;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(roles = "ORGANIZER")
    void registerMissione() throws Exception {
        var utente = mock(Utente.class);
        var file = new MockMultipartFile("File", InputStream.nullInputStream());
        var fileName = "file";
        var missioneDTO = mock(MissioneDTO.class);

        when(missioneDTO.getNome()).thenReturn("Help teaching a Pechino ");
        when(missioneDTO.getPaese()).thenReturn(1);
        when(missioneDTO.getCitta()).thenReturn("Pechino");
        when(missioneDTO.getDescrizione()).thenReturn("Descrizione di almeno 20 caratteri");
        when(missioneDTO.getDataInizio()).thenReturn(LocalDate.now().plusDays(2));
        when(missioneDTO.getDataFine()).thenReturn(LocalDate.now().plusDays(3));
        when(missioneDTO.getCompetenzeRichieste()).thenReturn("Competenze richieste");
        when(missioneDTO.getRequisitiExtra()).thenReturn("Requisiti extra");
        when(missioneDTO.getFoto()).thenReturn(file);
        when(missioneDTO.getCreatore()).thenReturn(utente);

        var paese = mock(Paese.class);

        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Italia");


        when(storageService.acceptUpload(file)).thenReturn(fileName);
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));
        when(missioneRepository.save(any(Missione.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(validator.validate(missioneDTO)).thenReturn(Set.of());

        var missione = gestioneMissione.registerMissione(missioneDTO);
        verify(validator).validate(missioneDTO);

        verify(missioneRepository).save(argThat(m -> Objects.equals(m.getId(), null)
                && Objects.equals(m.getNome(), missioneDTO.getNome())
                && Objects.equals(m.getPaese(), paese)
                && Objects.equals(m.getCitta(), missioneDTO.getCitta())
                && Objects.equals(m.getDescrizione(), missioneDTO.getDescrizione())
                && Objects.equals(m.getDataInizio(), missioneDTO.getDataInizio())
                && Objects.equals(m.getDataFine(), missioneDTO.getDataFine())
                && Objects.equals(m.getCompetenzeRichieste(), missioneDTO.getCompetenzeRichieste())
                && Objects.equals(m.getRequisitiExtra(), missioneDTO.getRequisitiExtra())
                && Objects.equals(m.getImmagine(), fileName)
                && Objects.equals(m.getStato(), Missione.MissioneStato.PENDING)
                && Objects.equals(m.getCreatore(), utente)));

        assertEquals(missioneDTO.getNome(), missione.getNome());
        assertEquals(paese, missione.getPaese());
        assertEquals(missioneDTO.getCitta(), missione.getCitta());
        assertEquals(missioneDTO.getDescrizione(), missione.getDescrizione());
        assertEquals(missioneDTO.getDataInizio(), missione.getDataInizio());
        assertEquals(missioneDTO.getDataFine(), missione.getDataFine());
        assertEquals(missioneDTO.getCompetenzeRichieste(), missione.getCompetenzeRichieste());
        assertEquals(missioneDTO.getRequisitiExtra(), missione.getRequisitiExtra());
        assertEquals(fileName, missione.getImmagine());
        assertEquals(Missione.MissioneStato.PENDING, missione.getStato());
        assertEquals(utente, missione.getCreatore());
    }

    @Test
    @WithMockUser(roles = {"VOLUNTEER", "MODERATOR", "ACCOUNT_MANAGER", "ANONYMOUS"})
    void registerMissioneNotOrganizerFails() throws Exception {
        assertThrows(AuthorizationDeniedException.class, () -> gestioneMissione.registerMissione(mock(MissioneDTO.class)));

    }

    @Test
    @WithMockUser(roles = "ORGANIZER")
    void registerMissioneValidationFails() throws Exception {
        var missioneDTO = mock(MissioneDTO.class);
        var constraintViolation = (ConstraintViolation<MissioneDTO>) mock(ConstraintViolation.class);
        when(validator.validate(missioneDTO)).thenReturn(Set.of(constraintViolation));

        assertThrows(ConstraintViolationException.class, () -> gestioneMissione.registerMissione(missioneDTO));
        verify(validator).validate(missioneDTO);
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void acceptMissione() {
        var id = 1L;
        var missione = mock(Missione.class);

        when(missione.getStato()).thenReturn(Missione.MissioneStato.PENDING);

        when(missioneRepository.findById(id)).thenReturn(Optional.of(missione));

        assertDoesNotThrow(() -> gestioneMissione.acceptMissione(id));
        verify(missione).accettaMissione();
    }

    @Test
    @WithMockUser(roles = {"VOLUNTEER", "ORGANIZER", "ACCOUNT_MANAGER", "ANONYMOUS"})
    void acceptMissioneNotModeratorFails() {
        assertThrows(AuthorizationDeniedException.class, () -> gestioneMissione.acceptMissione(1L));

    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void acceptMissioneNotPending() {
        var missione = mock(Missione.class);

        for (var stato : Missione.MissioneStato.values()) {
            if (stato.equals(Missione.MissioneStato.PENDING)) continue;
            var id = 1L;

            when(missione.getStato()).thenReturn(stato);

            when(missioneRepository.findById(id)).thenReturn(Optional.of(missione));

            assertThrows(MissioneNotAcceptableException.class, () -> gestioneMissione.acceptMissione(id));
            verify(missione, never()).accettaMissione();
        }
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void acceptMissioneNotExists() {
        var id = 1L;
        when(missioneRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(MissioneNotFoundException.class, () -> gestioneMissione.acceptMissione(id));
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void rejectMissione() {
        var id = 1L;
        var missione = mock(Missione.class);

        when(missione.getStato()).thenReturn(Missione.MissioneStato.PENDING);

        when(missioneRepository.findById(id)).thenReturn(Optional.of(missione));

        assertDoesNotThrow(() -> gestioneMissione.rejectMissione(id));
        verify(missione).rifiutaMissione();
    }

    @Test
    @WithMockUser(roles = {"VOLUNTEER", "ORGANIZER", "ACCOUNT_MANAGER", "ANONYMOUS"})
    void rejectMissioneNotModerator() {
        assertThrows(AuthorizationDeniedException.class, () -> gestioneMissione.rejectMissione(1L));
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void rejectMissioneNotPending() {
        var missione = mock(Missione.class);

        for (var stato : Missione.MissioneStato.values()) {
            if (stato.equals(Missione.MissioneStato.PENDING)) continue;
            var id = 1L;

            when(missione.getStato()).thenReturn(stato);

            when(missioneRepository.findById(id)).thenReturn(Optional.of(missione));

            assertThrows(MissioneNotAcceptableException.class, () -> gestioneMissione.rejectMissione(id));
            verify(missione, never()).rifiutaMissione();
        }
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void rejectMissioneNotExists() {

        var id = 1L;

        when(missioneRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(MissioneNotFoundException.class, () -> gestioneMissione.rejectMissione(id));
    }

    @Test
    void getMissioniAperte() {
        var page = (Page<Missione>) mock(Page.class);
        when(missioneRepository.findByPaeseAndInternalStatoAndDataFineAfter(any(Paese.class), any(Missione.InternalMissioneStato.class), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(page);
        when(paeseRepository.findById(anyInt())).thenReturn(Optional.of(new Paese(1, "Italia")));
        var res = gestioneMissione.getMissioniAperte(1, 0, 1);
        assertEquals(page, res);
    }

    @Test
    void getMissioniApertePaeseNotFound() {
        var page1 = (Page<Missione>) mock(Page.class);
        var page2 = (Page<Missione>) mock(Page.class);
        when(missioneRepository.findByPaeseAndInternalStatoAndDataFineAfter(any(Paese.class), any(Missione.InternalMissioneStato.class), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(page1);
        when(missioneRepository.findByInternalStatoAndDataFineAfter(any(Missione.InternalMissioneStato.class), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(page2);
        when(paeseRepository.findById(anyInt())).thenReturn(Optional.empty());
        var res = gestioneMissione.getMissioniAperte(1, 0, 1);
        assertSame(page2, res);
        assertNotSame(page1, res);
    }

    @Test
    void getMissioniApertePaeseNull() {
        var page1 = (Page<Missione>) mock(Page.class);
        var page2 = (Page<Missione>) mock(Page.class);
        when(missioneRepository.findByPaeseAndInternalStatoAndDataFineAfter(any(Paese.class), any(Missione.InternalMissioneStato.class), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(page1);
        when(missioneRepository.findByInternalStatoAndDataFineAfter(any(Missione.InternalMissioneStato.class), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(page2);
        when(paeseRepository.findById(anyInt())).thenReturn(Optional.of(new Paese(1, "Italia")));
        var res = gestioneMissione.getMissioniAperte(null, 0, 1);
        assertSame(page2, res);
        assertNotSame(page1, res);
    }

    @Test
    void getMissioniApertePaeseZero() {
        var page1 = (Page<Missione>) mock(Page.class);
        var page2 = (Page<Missione>) mock(Page.class);
        when(missioneRepository.findByPaeseAndInternalStatoAndDataFineAfter(any(Paese.class), any(Missione.InternalMissioneStato.class), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(page1);
        when(missioneRepository.findByInternalStatoAndDataFineAfter(any(Missione.InternalMissioneStato.class), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(page2);
        when(paeseRepository.findById(anyInt())).thenReturn(Optional.of(new Paese(1, "Italia")));
        var res = gestioneMissione.getMissioniAperte(0, 0, 1);
        assertSame(page2, res);
        assertNotSame(page1, res);
    }

    @Test
    void getMissioniApertePageNumberNegative() {
        assertThrows(IllegalArgumentException.class, () -> gestioneMissione.getMissioniAperte(1, -1, 1));
    }

    @Test
    void getMissioniApertePageSizeZero() {
        assertThrows(IllegalArgumentException.class, () -> gestioneMissione.getMissioniAperte(1, 1, 0));
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void getMissioniPending() {
        var page = (Page<Missione>) mock(Page.class);
        when(missioneRepository.findByInternalStatoAndDataFineAfter(any(Missione.InternalMissioneStato.class), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(page);
        var res = gestioneMissione.getMissioniPending(0, 1);
        assertEquals(page, res);

    }

    @Test
    @WithMockUser(roles = {"VOLUNTEER", "ORGANIZER", "ACCOUNT_MANAGER", "ANONYMOUS"})
    void getMissioniPendingNotModerator() {
        assertThrows(AuthorizationDeniedException.class, () -> gestioneMissione.getMissioniPending(0, 1));

    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void getMissioniPendingPageNumberNegative() {
        assertThrows(IllegalArgumentException.class, () -> gestioneMissione.getMissioniPending(-1, 1));
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void getMissioniPendingPageSizeZero() {
        assertThrows(IllegalArgumentException.class, () -> gestioneMissione.getMissioniPending(0, 0));
    }

    @Test
    void getMissioniOrganizzatore() {
        var context = SecurityContextHolder.getContext();
        var utente = mock(Utente.class);
        var authentication = new TestingAuthenticationToken(utente, null, "ROLE_ORGANIZER");
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        var page = (Page<Missione>) mock(Page.class);

        when(missioneRepository.findByCreatore(eq(utente), any(Pageable.class))).thenReturn(page);

        var res = gestioneMissione.getMissioniOrganizzatore(0, 1);
        assertEquals(page, res);
    }

    @Test
    @WithMockUser(roles = {"VOLUNTEER", "MODERATOR", "ACCOUNT_MANAGER", "ANONYMOUS"})
    void getMissioniOrganizzatoreNotOrganizer() {
        assertThrows(AuthorizationDeniedException.class, () -> gestioneMissione.getMissioniOrganizzatore(0, 1));
    }

    @Test
    void getImmagineMissione() throws IOException {
        var filename = "immagine";
        var resource = mock(FileSystemResource.class);
        when(storageService.downloadFile(any(String.class))).thenReturn(resource);
        var res = gestioneMissione.getImmagineMissione(filename);
        assertEquals(resource, res);
    }

    @Test
    void getImmagineMissioneImageNotFound() throws IOException {
        var filename = "immagine";
        when(storageService.downloadFile(any(String.class))).thenThrow(new IOException());
        var res = assertDoesNotThrow(() -> gestioneMissione.getImmagineMissione(filename));
        assertInstanceOf(Resource.class, res);
    }

    @Test
    void getMissioniId() {
        var missione = mock(Missione.class);
        var id = 1L;
        when(missioneRepository.findById(id)).thenReturn(Optional.of(missione));

        var res = assertDoesNotThrow(() -> gestioneMissione.getMissioneById(id));
        assertEquals(missione, res);
    }

    @Test
    void getMissioniIdNotFound() {
        var id = 1L;
        when(missioneRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> gestioneMissione.getMissioneById(id));
    }


}
