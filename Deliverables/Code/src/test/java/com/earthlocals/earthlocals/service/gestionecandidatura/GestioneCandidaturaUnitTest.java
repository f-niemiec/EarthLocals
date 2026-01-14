package com.earthlocals.earthlocals.service.gestionecandidatura;

import com.earthlocals.earthlocals.config.TestAppConfig;
import com.earthlocals.earthlocals.model.*;
import com.earthlocals.earthlocals.service.gestionecandidature.GestioneCandidatura;
import com.earthlocals.earthlocals.service.gestionecandidature.dto.CandidaturaDTO;
import com.earthlocals.earthlocals.service.gestionecandidature.exceptions.CandidaturaAlreadyExistsException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {
        TestAppConfig.class,
        GestioneCandidatura.class
})
@ExtendWith(SpringExtension.class)
public class GestioneCandidaturaUnitTest {
    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private CandidaturaRepository candidaturaRepository;
    @MockitoBean
    private MissioneRepository missioneRepository;
    @MockitoBean
    private VolontarioRepository volontarioRepository;
    @MockitoBean
    private Validator validator;

    @MockitoSpyBean
    @Autowired
    private GestioneCandidatura gestioneCandidatura;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerCandidatura() throws Exception {
        var candidaturaDTO = mock(CandidaturaDTO.class);
        var volontario = mock(Volontario.class);
        var missione = mock(Missione.class);
        var missioneId = 1L;
        var candidatoId = 1L;

        when(candidaturaDTO.getMissioneId()).thenReturn(missioneId);
        when(candidaturaDTO.getCandidatoId()).thenReturn(candidatoId);

        when(missione.getStato()).thenReturn(Missione.MissioneStato.ACCETTATA);
        when(missioneRepository.findById(missioneId)).thenReturn(Optional.of(missione));
        when(volontarioRepository.findById(candidatoId)).thenReturn(Optional.of(volontario));
        when(gestioneCandidatura.hasVolontarioAlreadyApplied(missione, volontario)).thenReturn(false);
        when(candidaturaRepository.existsByMissioneAndCandidato(missione, volontario)).thenReturn(false);

        var res = assertDoesNotThrow(() -> gestioneCandidatura.registerCandidatura(candidaturaDTO));

        var candidaturaCaptor = ArgumentCaptor.forClass(Candidatura.class);
        verify(candidaturaRepository).save(candidaturaCaptor.capture());
        var savedCandidatura = candidaturaCaptor.getValue();

        assertNotNull(savedCandidatura);
        assertEquals(missione, savedCandidatura.getMissione());
        assertEquals(volontario, savedCandidatura.getCandidato());
        assertEquals(Candidatura.CandidaturaStato.IN_CORSO, savedCandidatura.getStato());
        assertNotNull(savedCandidatura.getDataCandidatura());
        assertSame(savedCandidatura, res);
    }

    @Test
    void registerCandidatureConstraintFails() throws Exception {
        var candidaturaDTO = mock(CandidaturaDTO.class);
        var constraintViolation = (ConstraintViolation<CandidaturaDTO>) mock(ConstraintViolation.class);

        when(validator.validate(candidaturaDTO)).thenReturn(Set.of(constraintViolation));

        assertThrows(ConstraintViolationException.class, () -> gestioneCandidatura.registerCandidatura(candidaturaDTO));

        verify(candidaturaRepository, times(0)).save(any());
    }

    @Test
    void registerCandidatureMissionDoesntExists() {
        var candidaturaDTO = mock(CandidaturaDTO.class);
        Long missioneId = 1L;
        Long candidatoId = 1L;

        when(candidaturaDTO.getMissioneId()).thenReturn(missioneId);
        when(candidaturaDTO.getCandidatoId()).thenReturn(candidatoId);

        when(missioneRepository.findById(missioneId))
                .thenReturn(Optional.empty());

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> gestioneCandidatura.registerCandidatura(candidaturaDTO)
        );

        verify(candidaturaRepository, never()).save(any());
    }

    @Test
    void registerCandidatureVolontarioDoesntExists() {
        var candidaturaDTO = mock(CandidaturaDTO.class);
        Long missioneId = 1L;
        Long candidatoId = 1L;

        when(candidaturaDTO.getMissioneId()).thenReturn(missioneId);
        when(candidaturaDTO.getCandidatoId()).thenReturn(candidatoId);

        when(candidaturaRepository.findById(candidatoId))
                .thenReturn(Optional.empty());

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> gestioneCandidatura.registerCandidatura(candidaturaDTO)
        );

        verify(candidaturaRepository, never()).save(any());
    }

    @Test
    void registerCandidatureAlreadyPresent() throws Exception {
        var candidaturaDTO = mock(CandidaturaDTO.class);
        var missione = mock(Missione.class);
        var volontario = mock(Volontario.class);

        Long missioneId = 1L;
        Long candidatoId = 1L;

        when(candidaturaDTO.getMissioneId()).thenReturn(missioneId);
        when(candidaturaDTO.getCandidatoId()).thenReturn(candidatoId);

        when(missione.getStato()).thenReturn(Missione.MissioneStato.ACCETTATA);
        when(missioneRepository.findById(missioneId)).thenReturn(Optional.of(missione));
        when(volontarioRepository.findById(candidatoId)).thenReturn(Optional.of(volontario));

        when(gestioneCandidatura.hasVolontarioAlreadyApplied(missione, volontario))
                .thenReturn(true);

        var exception = assertThrows(
                CandidaturaAlreadyExistsException.class,
                () -> gestioneCandidatura.registerCandidatura(candidaturaDTO)
        );

        verify(candidaturaRepository, never()).save(any());
    }

    @Test
    public void hasVolontarioAlreadyApplied() throws Exception {
        var candidaturaDTO = mock(CandidaturaDTO.class);
        var volontario = mock(Volontario.class);
        var missione = mock(Missione.class);
        var missioneId = 1L;
        var candidatoId = 1L;

        when(candidaturaDTO.getMissioneId()).thenReturn(missioneId);
        when(candidaturaDTO.getCandidatoId()).thenReturn(candidatoId);
        when(missione.getStato()).thenReturn(Missione.MissioneStato.ACCETTATA);
        when(missioneRepository.findById(missioneId)).thenReturn(Optional.of(missione));
        when(volontarioRepository.findById(candidatoId)).thenReturn(Optional.of(volontario));
        when(candidaturaRepository.existsByMissioneAndCandidato(missione, volontario)).thenReturn(false);

        boolean res = assertDoesNotThrow(() -> gestioneCandidatura.hasVolontarioAlreadyApplied(candidaturaDTO));

        assertFalse(res);
        verify(missioneRepository).findById(missioneId);
        verify(volontarioRepository).findById(candidatoId);
        verify(candidaturaRepository).existsByMissioneAndCandidato(missione, volontario);
    }

    @Test
    void alreadyAppliedCostraintFails() throws Exception {
        var candidaturaDTO = mock(CandidaturaDTO.class);
        var constraintViolation = (ConstraintViolation<CandidaturaDTO>) mock(ConstraintViolation.class);

        when(validator.validate(candidaturaDTO)).thenReturn(Set.of(constraintViolation));

        assertThrows(ConstraintViolationException.class, () -> gestioneCandidatura.hasVolontarioAlreadyApplied(candidaturaDTO));

        verify(candidaturaRepository, times(0)).save(any());
    }

    @Test
    void alreadyAppliedMissionDoesntExist() throws Exception {
        var candidaturaDTO = mock(CandidaturaDTO.class);
        var missioneId = 1L;
        var candidatoId = 1L;

        when(candidaturaDTO.getMissioneId()).thenReturn(missioneId);
        when(candidaturaDTO.getCandidatoId()).thenReturn(candidatoId);

        when(missioneRepository.findById(missioneId))
                .thenReturn(Optional.empty());

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> gestioneCandidatura.hasVolontarioAlreadyApplied(candidaturaDTO)
        );

        verify(candidaturaRepository, never()).save(any());
    }

    @Test
    void alreadyAppliedCandidateDoesntExist() throws Exception {
        var candidaturaDTO = mock(CandidaturaDTO.class);
        var missioneId = 1L;
        var candidatoId = 1L;

        when(candidaturaDTO.getMissioneId()).thenReturn(missioneId);
        when(candidaturaDTO.getCandidatoId()).thenReturn(candidatoId);
        when(candidaturaDTO.getMissioneId()).thenReturn(missioneId);
        when(candidaturaDTO.getCandidatoId()).thenReturn(candidatoId);

        when(missioneRepository.findById(candidatoId))
                .thenReturn(Optional.empty());

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> gestioneCandidatura.hasVolontarioAlreadyApplied(candidaturaDTO)
        );

        verify(candidaturaRepository, never()).save(any());
    }

    @Test
    void hasVolontarioAlreadyAppliedTwo() throws Exception {
        var missione = mock(Missione.class);
        var volontario = mock(Volontario.class);

        when(candidaturaRepository.existsByMissioneAndCandidato(missione, volontario))
                .thenReturn(false);

        boolean result = gestioneCandidatura.hasVolontarioAlreadyApplied(missione, volontario);

        assertFalse(result);

        verify(candidaturaRepository).existsByMissioneAndCandidato(missione, volontario);
    }

    @Test
    void alreadyAppliedTwoCostraintFails() throws Exception {
        var missione = mock(Missione.class);
        var volontario = mock(Volontario.class);

        when(candidaturaRepository.existsByMissioneAndCandidato(missione, volontario))
                .thenReturn(true);

        boolean result = gestioneCandidatura.hasVolontarioAlreadyApplied(missione, volontario);

        assertTrue(result);

        verify(candidaturaRepository).existsByMissioneAndCandidato(missione, volontario);
    }

    @Test
    void removeCandidatura() throws Exception {
        var candidaturaDTO = mock(CandidaturaDTO.class);
        var volontario = mock(Volontario.class);
        var missione = mock(Missione.class);
        var candidatura = mock(Candidatura.class);

        var missioneId = 1L;
        var candidatoId = 1L;
        when(candidaturaDTO.getMissioneId()).thenReturn(missioneId);
        when(candidaturaDTO.getCandidatoId()).thenReturn(candidatoId);
        when(missioneRepository.findById(missioneId)).thenReturn(Optional.of(missione));
        when(volontarioRepository.findById(candidatoId)).thenReturn(Optional.of(volontario));
        when(gestioneCandidatura.hasVolontarioAlreadyApplied(missione, volontario)).thenReturn(true);
        when(candidaturaRepository.findByMissioneAndCandidato(missione, volontario))
                .thenReturn(candidatura);

        assertDoesNotThrow(() -> gestioneCandidatura.removeCandidatura(candidaturaDTO));

        verify(candidaturaRepository).findByMissioneAndCandidato(missione, volontario);
        verify(candidaturaRepository).delete(candidatura);
    }

    @Test
    void removeCandidaturaConstraintFails() throws Exception {
        var candidaturaDTO = mock(CandidaturaDTO.class);
        var constraintViolation = (ConstraintViolation<CandidaturaDTO>) mock(ConstraintViolation.class);

        when(validator.validate(candidaturaDTO)).thenReturn(Set.of(constraintViolation));

        assertThrows(ConstraintViolationException.class, () -> gestioneCandidatura.removeCandidatura(candidaturaDTO));

        verify(candidaturaRepository, times(0)).save(any());
    }

    @Test
    void removeCandidatureMissionDoesntExist() throws Exception {
        var candidaturaDTO = mock(CandidaturaDTO.class);
        var missioneId = 1L;
        var candidatoId = 1L;

        when(candidaturaDTO.getMissioneId()).thenReturn(missioneId);
        when(candidaturaDTO.getCandidatoId()).thenReturn(candidatoId);

        when(missioneRepository.findById(missioneId))
                .thenReturn(Optional.empty());

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> gestioneCandidatura.removeCandidatura(candidaturaDTO)
        );

        verify(candidaturaRepository, never()).delete(any());
    }

    @Test
    void removeCandidatureCandidateDoesntExist() throws Exception {
        var candidaturaDTO = mock(CandidaturaDTO.class);
        var missioneId = 1L;
        var candidatoId = 1L;

        when(candidaturaDTO.getMissioneId()).thenReturn(missioneId);
        when(candidaturaDTO.getCandidatoId()).thenReturn(candidatoId);

        when(missioneRepository.findById(candidatoId))
                .thenReturn(Optional.empty());

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> gestioneCandidatura.removeCandidatura(candidaturaDTO)
        );

        verify(candidaturaRepository, never()).delete(any());
    }

    @Test
    void removeCandidaturaAlreadyApplied() throws Exception {
        var candidaturaDTO = mock(CandidaturaDTO.class);
        var volontario = mock(Volontario.class);
        var missione = mock(Missione.class);
        var candidatura = mock(Candidatura.class);

        Long missioneId = 1L;
        Long candidatoId = 1L;

        when(candidaturaDTO.getMissioneId()).thenReturn(missioneId);
        when(candidaturaDTO.getCandidatoId()).thenReturn(candidatoId);

        when(missioneRepository.findById(missioneId))
                .thenReturn(Optional.of(missione));
        when(volontarioRepository.findById(candidatoId))
                .thenReturn(Optional.of(volontario));

        when(candidaturaRepository.existsByMissioneAndCandidato(missione, volontario))
                .thenReturn(true);
        when(candidaturaRepository.findByMissioneAndCandidato(missione, volontario))
                .thenReturn(candidatura);

        gestioneCandidatura.removeCandidatura(candidaturaDTO);

        verify(candidaturaRepository).delete(candidatura);
    }

    @Test
    void removeCandidaturaNotFound() throws Exception {
        var candidaturaDTO = mock(CandidaturaDTO.class);
        var missione = mock(Missione.class);
        var volontario = mock(Volontario.class);
        Long missioneId = 1L;
        Long candidatoId = 1L;
        when(candidaturaDTO.getMissioneId()).thenReturn(missioneId);
        when(candidaturaDTO.getCandidatoId()).thenReturn(candidatoId);

        when(missioneRepository.findById(missioneId))
                .thenReturn(Optional.of(missione));
        when(volontarioRepository.findById(candidatoId))
                .thenReturn(Optional.of(volontario));

        when(candidaturaRepository.existsByMissioneAndCandidato(missione, volontario))
                .thenReturn(false);

        gestioneCandidatura.removeCandidatura(candidaturaDTO);

        verify(candidaturaRepository, never())
                .findByMissioneAndCandidato(any(), any());
        verify(candidaturaRepository, never())
                .delete(any());
    }


    @Test
    @WithMockUser(roles = "ORGANIZER")
    void acceptCandidatura() {
        var candidaturaId = 1L;

        var candidatura = mock(Candidatura.class);
        var missione = mock(Missione.class);
        var creatore = mock(Utente.class);

        when(creatore.getId()).thenReturn(1L);
        when(missione.getCreatore()).thenReturn(creatore);
        when(candidatura.getMissione()).thenReturn(missione);

        when(candidaturaRepository.findById(candidaturaId))
                .thenReturn(Optional.of(candidatura));

        var authentication = new TestingAuthenticationToken(creatore, null, "ROLE_ORGANIZER");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var result = gestioneCandidatura.acceptCandidatura(candidaturaId);

        assertTrue(result);

        verify(candidatura)
                .setStato(Candidatura.CandidaturaStato.ACCETTATA);
    }

    @Test
    @WithMockUser(roles = {"VOLUNTEER", "MODERATOR", "ACCOUNT_MANAGER"})
    void acceptCandidaturaNotOrganizerFails() {
        var candidaturaId = 1L;
        var candidatura = mock(Candidatura.class);
        when(candidaturaRepository.findById(candidaturaId))
                .thenReturn(Optional.of(candidatura));
        assertThrows(AuthorizationDeniedException.class, () -> gestioneCandidatura.acceptCandidatura(candidaturaId));
        verify(candidatura, never())
                .setStato(any());
    }

    @Test
    @WithAnonymousUser
    void acceptCandidaturaAnonymousFails() {
        var candidaturaId = 1L;
        var candidatura = mock(Candidatura.class);
        when(candidaturaRepository.findById(candidaturaId))
                .thenReturn(Optional.of(candidatura));
        assertThrows(AuthorizationDeniedException.class, () -> gestioneCandidatura.acceptCandidatura(candidaturaId));
        verify(candidatura, never())
                .setStato(any());
    }

    @Test
    @WithMockUser(roles = "ORGANIZER")
    void acceptCandidaturaDoesntExists() throws Exception {
        Long candidaturaId = 1L;
        when(candidaturaRepository.findById(candidaturaId))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> gestioneCandidatura.acceptCandidatura(candidaturaId)
        );
    }

    @Test
    void acceptCandidaturaIsNotCreatore() throws Exception {
        var candidatura = mock(Candidatura.class);
        var missione = mock(Missione.class);
        var creatore = mock(Utente.class);
        var utenteLoggato = mock(Utente.class);
        Long creatoreId = 1L;
        Long utenteLoggatoId = 2L;

        when(creatore.getId()).thenReturn(creatoreId);
        when(utenteLoggato.getId()).thenReturn(utenteLoggatoId);

        when(missione.getCreatore()).thenReturn(creatore);
        when(candidatura.getMissione()).thenReturn(missione);

        when(candidaturaRepository.findById(1L))
                .thenReturn(Optional.of(candidatura));

        var auth = new TestingAuthenticationToken(utenteLoggato, null, "ROLE_ORGANIZER");
        SecurityContextHolder.getContext().setAuthentication(auth);

        var result = gestioneCandidatura.acceptCandidatura(1L);

        assertFalse(result);
        verify(candidatura, never())
                .setStato(Candidatura.CandidaturaStato.ACCETTATA);
    }

    @Test
    void rejectCandidatura() {
        var candidaturaId = 1L;

        var candidatura = mock(Candidatura.class);
        var missione = mock(Missione.class);
        var creatore = mock(Utente.class);

        when(creatore.getId()).thenReturn(1L);
        when(missione.getCreatore()).thenReturn(creatore);
        when(candidatura.getMissione()).thenReturn(missione);

        when(candidaturaRepository.findById(candidaturaId))
                .thenReturn(Optional.of(candidatura));

        var authentication = new TestingAuthenticationToken(creatore, null, "ROLE_ORGANIZER");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var result = gestioneCandidatura.rejectCandidatura(candidaturaId);

        assertTrue(result);

        verify(candidatura)
                .setStato(Candidatura.CandidaturaStato.RIFIUTATA);
    }

    @Test
    @WithMockUser(roles = {"VOLUNTEER", "MODERATOR", "ACCOUNT_MANAGER"})
    void rejectCandidaturaNotOrganizerFails() {
        var candidaturaId = 1L;
        var candidatura = mock(Candidatura.class);
        when(candidaturaRepository.findById(candidaturaId))
                .thenReturn(Optional.of(candidatura));
        assertThrows(AuthorizationDeniedException.class, () -> gestioneCandidatura.rejectCandidatura(candidaturaId));
        verify(candidatura, never())
                .setStato(any());
    }

    @Test
    @WithAnonymousUser
    void rejectCandidaturaAnonymousFails() {
        var candidaturaId = 1L;
        var candidatura = mock(Candidatura.class);
        when(candidaturaRepository.findById(candidaturaId))
                .thenReturn(Optional.of(candidatura));
        assertThrows(AuthorizationDeniedException.class, () -> gestioneCandidatura.rejectCandidatura(candidaturaId));
        verify(candidatura, never())
                .setStato(any());
    }


    @Test
    @WithMockUser(roles = "ORGANIZER")
    void rejectCandidaturaDoesntExists() throws Exception {
        Long candidaturaId = 1L;
        when(candidaturaRepository.findById(candidaturaId))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> gestioneCandidatura.rejectCandidatura(candidaturaId)
        );
    }

    @Test
    void rejectCandidaturaIsNotCreatore() throws Exception {
        var candidatura = mock(Candidatura.class);
        var missione = mock(Missione.class);
        var creatore = mock(Utente.class);
        var utenteLoggato = mock(Utente.class);
        Long creatoreId = 1L;
        Long utenteLoggatoId = 2L;

        when(creatore.getId()).thenReturn(creatoreId);
        when(utenteLoggato.getId()).thenReturn(utenteLoggatoId);

        when(missione.getCreatore()).thenReturn(creatore);
        when(candidatura.getMissione()).thenReturn(missione);

        when(candidaturaRepository.findById(1L))
                .thenReturn(Optional.of(candidatura));

        var auth = new TestingAuthenticationToken(utenteLoggato, null, "ROLE_ORGANIZER");
        SecurityContextHolder.getContext().setAuthentication(auth);

        var result = gestioneCandidatura.rejectCandidatura(1L);

        assertFalse(result);
        verify(candidatura, never())
                .setStato(Candidatura.CandidaturaStato.RIFIUTATA);
    }

    @Test
    void getCandidatureVolontario() throws Exception {
        var volontario = mock(Volontario.class);
        var auth = new TestingAuthenticationToken(volontario, null, "ROLE_VOLUNTEER");
        var page = mock(Page.class);
        int pageNumber = 1;
        int pageSize = 6;
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(candidaturaRepository.findByCandidato(eq(volontario), any(Pageable.class)))
                .thenReturn(page);

        var result = gestioneCandidatura.getCandidatureVolontario(pageNumber, pageSize);
        assertEquals(page, result);
        verify(candidaturaRepository)
                .findByCandidato(eq(volontario), any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = {"ORGANIZER", "MODERATOR", "ACCOUNT_MANAGER"})
    void getCandidatureVolontarioNotVolunteerFails() {
        int pageNumber = 1;
        int pageSize = 6;

        assertThrows(AuthorizationDeniedException.class, () ->
                gestioneCandidatura.getCandidatureVolontario(pageNumber, pageSize)
        );
    }

    @Test
    @WithAnonymousUser
    void getCandidatureVolontarioAnonymousFails() {
        int pageNumber = 1;
        int pageSize = 6;

        assertThrows(AuthorizationDeniedException.class, () ->
                gestioneCandidatura.getCandidatureVolontario(pageNumber, pageSize)
        );
    }

    @Test
    void getEsperienzeVolontario() {
        var utente = mock(Utente.class);
        var auth = new TestingAuthenticationToken(utente, null, "ROLE_VOLUNTEER");
        var page = mock(Page.class);
        int pageNumber = 1;
        int pageSize = 6;

        SecurityContextHolder.getContext().setAuthentication(auth);

        when(candidaturaRepository
                .findByCandidatoAndStatoAndMissioneInternalStatoAndMissioneDataFineBefore(
                        eq(utente),
                        eq(Candidatura.CandidaturaStato.ACCETTATA),
                        eq(Missione.InternalMissioneStato.ACCETTATA),
                        any(LocalDate.class),
                        any(Pageable.class)))
                .thenReturn(page);

        var result = gestioneCandidatura.getEsperienzeVolontario(pageNumber, pageSize);

        assertEquals(page, result);
        verify(candidaturaRepository)
                .findByCandidatoAndStatoAndMissioneInternalStatoAndMissioneDataFineBefore(
                        eq(utente),
                        eq(Candidatura.CandidaturaStato.ACCETTATA),
                        eq(Missione.InternalMissioneStato.ACCETTATA),
                        any(LocalDate.class),
                        any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = {"ORGANIZER", "MODERATOR", "ACCOUNT_MANAGER"})
    void getEsperienzeVolontarioNotVolunteerFails() {
        assertThrows(AuthorizationDeniedException.class, () -> gestioneCandidatura.getEsperienzeVolontario(0, 6));
    }

    @Test
    @WithAnonymousUser
    void getEsperienzeVolontarioAnonymousFails() {
        assertThrows(AuthorizationDeniedException.class, () -> gestioneCandidatura.getEsperienzeVolontario(0, 6));
    }

    @Test
    void getEsperienzeVolontarioEmptyPage() {
        var utente = mock(Utente.class);
        var auth = new TestingAuthenticationToken(utente, null, "ROLE_VOLUNTEER");
        var emptyPage = mock(Page.class);
        int pageNumber = 1;
        int pageSize = 6;

        SecurityContextHolder.getContext().setAuthentication(auth);

        when(candidaturaRepository
                .findByCandidatoAndStatoAndMissioneInternalStatoAndMissioneDataFineBefore(
                        eq(utente),
                        eq(Candidatura.CandidaturaStato.ACCETTATA),
                        eq(Missione.InternalMissioneStato.ACCETTATA),
                        any(LocalDate.class),
                        any(Pageable.class)))
                .thenReturn(emptyPage);

        var result = gestioneCandidatura.getEsperienzeVolontario(pageNumber, pageSize);

        assertEquals(emptyPage, result);
        verify(candidaturaRepository)
                .findByCandidatoAndStatoAndMissioneInternalStatoAndMissioneDataFineBefore(
                        eq(utente),
                        eq(Candidatura.CandidaturaStato.ACCETTATA),
                        eq(Missione.InternalMissioneStato.ACCETTATA),
                        any(LocalDate.class),
                        any(Pageable.class));
    }

    @Test
    void getRichiesteCandidatura() {
        var utente = mock(Utente.class);
        var page = mock(Page.class);
        int pageNumber = 1;
        int pageSize = 6;

        var auth = new TestingAuthenticationToken(utente, null, "ROLE_ORGANIZER");
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(candidaturaRepository.findByOrganizzatore(eq(utente), any(Pageable.class)))
                .thenReturn(page);

        var result = gestioneCandidatura.getRichiesteCandidatura(pageNumber, pageSize);

        assertEquals(page, result);
        verify(candidaturaRepository)
                .findByOrganizzatore(eq(utente), any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = {"VOLUNTEER", "MODERATOR", "ACCOUNT_MANAGER"})
    void getRichiesteCandidaturaNotOrganizerFails() {
        assertThrows(AuthorizationDeniedException.class, () -> gestioneCandidatura.getRichiesteCandidatura(1, 6));
    }

    @Test
    @WithAnonymousUser
    void getRichiesteCandidaturaAnonymousFails() {
        assertThrows(AuthorizationDeniedException.class, () -> gestioneCandidatura.getRichiesteCandidatura(1, 6));
    }


    @Test
    void getRichiesteCandidaturaEmptyPage() {
        var utente = mock(Utente.class);
        var auth = new TestingAuthenticationToken(utente, null, "ROLE_ORGANIZER");
        var emptyPage = mock(Page.class);
        int pageNumber = 1;
        int pageSize = 6;

        SecurityContextHolder.getContext().setAuthentication(auth);

        when(candidaturaRepository.findByOrganizzatore(eq(utente), any(Pageable.class)))
                .thenReturn(emptyPage);

        var result = gestioneCandidatura.getRichiesteCandidatura(pageNumber, pageSize);

        assertEquals(emptyPage, result);
        verify(candidaturaRepository)
                .findByOrganizzatore(eq(utente), any(Pageable.class));
    }


}
