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


}
