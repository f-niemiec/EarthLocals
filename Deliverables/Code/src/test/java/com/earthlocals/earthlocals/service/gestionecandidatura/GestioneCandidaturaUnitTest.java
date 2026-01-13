package com.earthlocals.earthlocals.service.gestionecandidatura;

import com.earthlocals.earthlocals.model.*;
import com.earthlocals.earthlocals.service.gestionecandidature.GestioneCandidatura;
import com.earthlocals.earthlocals.service.gestionecandidature.dto.CandidaturaDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class GestioneCandidaturaUnitTest {
    @Mock
    private CandidaturaRepository candidaturaRepository;
    @Mock
    private MissioneRepository missioneRepository;
    @Mock
    private VolontarioRepository volontarioRepository;
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

}
