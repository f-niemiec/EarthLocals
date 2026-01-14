package com.earthlocals.earthlocals.service.gestionecandidatura;

import com.earthlocals.earthlocals.model.*;
import com.earthlocals.earthlocals.service.gestionecandidature.GestioneCandidatura;
import com.earthlocals.earthlocals.service.gestionecandidature.dto.CandidaturaDTO;
import com.earthlocals.earthlocals.service.gestionecandidature.exceptions.CandidaturaAlreadyExistsException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class GestioneCandidaturaUnitTest {
    @Mock
    private CandidaturaRepository candidaturaRepository;
    @Mock
    private MissioneRepository missioneRepository;
    @Mock
    private VolontarioRepository volontarioRepository;
    @Mock
    private Validator validator;
    @Spy
    @InjectMocks
    private GestioneCandidatura gestioneCandidatura;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerCandidatura() throws Exception{
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
    void registerCandidatureConstraintFails() throws Exception{
        var candidaturaDTO = mock(CandidaturaDTO.class);
        var constraintViolation = (ConstraintViolation<CandidaturaDTO>) mock(ConstraintViolation.class);

        when(validator.validate(candidaturaDTO)).thenReturn(Set.of(constraintViolation));

        assertThrows(ConstraintViolationException.class, () -> gestioneCandidatura.registerCandidatura(candidaturaDTO));

        verify(candidaturaRepository, times(0)).save(any());
    }

    @Test
    void registerCandidatureMissionDoesntExists(){
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
    void registerCandidatureVolontarioDoesntExists(){
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
    void registerCandidatureAlreadyPresent() throws Exception{
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
    public void hasVolontarioAlreadyApplied() throws Exception{
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
    void alreadyAppliedCostraintFails() throws Exception{
        var candidaturaDTO = mock(CandidaturaDTO.class);
        var constraintViolation = (ConstraintViolation<CandidaturaDTO>) mock(ConstraintViolation.class);

        when(validator.validate(candidaturaDTO)).thenReturn(Set.of(constraintViolation));

        assertThrows(ConstraintViolationException.class, () -> gestioneCandidatura.hasVolontarioAlreadyApplied(candidaturaDTO));

        verify(candidaturaRepository, times(0)).save(any());
    }

    @Test
    void alreadyAppliedMissionDoesntExist() throws Exception{
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
    void alreadyAppliedCandidateDoesntExist() throws Exception{
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
    void hasVolontarioAlreadyAppliedTwo() throws Exception{
        var missione = mock(Missione.class);
        var volontario = mock(Volontario.class);

        when(candidaturaRepository.existsByMissioneAndCandidato(missione, volontario))
                .thenReturn(false);

        boolean result = gestioneCandidatura.hasVolontarioAlreadyApplied(missione, volontario);

        assertFalse(result);

        verify(candidaturaRepository).existsByMissioneAndCandidato(missione, volontario);
    }

    @Test
    void alreadyAppliedTwoCostraintFails() throws Exception{
        var missione = mock(Missione.class);
        var volontario = mock(Volontario.class);

        when(candidaturaRepository.existsByMissioneAndCandidato(missione, volontario))
                .thenReturn(true);

        boolean result = gestioneCandidatura.hasVolontarioAlreadyApplied(missione, volontario);

        assertTrue(result);

        verify(candidaturaRepository).existsByMissioneAndCandidato(missione, volontario);
    }

    @Test
    void removeCandidatura() throws Exception{
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
    void removeCandidaturaConstraintFails() throws Exception{
        var candidaturaDTO = mock(CandidaturaDTO.class);
        var constraintViolation = (ConstraintViolation<CandidaturaDTO>) mock(ConstraintViolation.class);

        when(validator.validate(candidaturaDTO)).thenReturn(Set.of(constraintViolation));

        assertThrows(ConstraintViolationException.class, () -> gestioneCandidatura.removeCandidatura(candidaturaDTO));

        verify(candidaturaRepository, times(0)).save(any());
    }

    @Test
    void removeCandidatureMissionDoesntExist() throws Exception{
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
    void removeCandidatureCandidateDoesntExist() throws Exception{
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
    void removeCandidaturaAlreadyApplied() throws Exception{
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
    void removeCandidaturaNotFound() throws Exception{
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

        var authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(creatore);

        var result = gestioneCandidatura.acceptCandidatura(candidaturaId);

        assertTrue(result);

        verify(candidatura)
                .setStato(Candidatura.CandidaturaStato.ACCETTATA);
    }

    @Test
    void acceptCandidaturaDoesntExists() throws Exception{
        Long candidaturaId = 1L;
        when(candidaturaRepository.findById(candidaturaId))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> gestioneCandidatura.acceptCandidatura(candidaturaId)
        );
    }

    @Test
    void acceptCandidaturaIsNotOrganizer() throws Exception{
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

        var auth = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(auth.getPrincipal()).thenReturn(utenteLoggato);

        var result = gestioneCandidatura.acceptCandidatura(1L);

        assertFalse(result);
        verify(candidatura, never())
                .setStato(Candidatura.CandidaturaStato.ACCETTATA);
    }

}
