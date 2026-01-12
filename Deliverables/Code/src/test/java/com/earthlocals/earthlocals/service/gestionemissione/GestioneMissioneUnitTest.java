package com.earthlocals.earthlocals.service.gestionemissione;

import com.earthlocals.earthlocals.model.*;
import com.earthlocals.earthlocals.service.gestionemissioni.GestioneMissione;
import com.earthlocals.earthlocals.service.gestionemissioni.dto.MissioneDTO;
import com.earthlocals.earthlocals.service.gestionemissioni.exceptions.MissioneNotAcceptableException;
import com.earthlocals.earthlocals.service.gestionemissioni.pictures.PicturesFilesystemStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GestioneMissioneUnitTest {
    @Mock
    private MissioneRepository missioneRepository;
    @Mock
    private PicturesFilesystemStorage storageService;
    @Mock
    private PaeseRepository paeseRepository;
    @InjectMocks
    private GestioneMissione gestioneMissione;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerMissione() throws Exception {
        var utente = mock(Utente.class);
        var file = new MockMultipartFile("File", InputStream.nullInputStream());
        var fileName = "file";
        var missioneDTO = mock(MissioneDTO.class);
        var paese = mock(Paese.class);

        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Italia");

        when(missioneDTO.getNome()).thenReturn("Help teaching a Pechino ");
        when(missioneDTO.getPaese()).thenReturn(1);
        when(missioneDTO.getCitta()).thenReturn("Salerno");
        when(missioneDTO.getDescrizione()).thenReturn("Salerno");
        when(missioneDTO.getDataInizio()).thenReturn(LocalDate.now().plusDays(2));
        when(missioneDTO.getDataFine()).thenReturn(LocalDate.now().plusDays(3));
        when(missioneDTO.getCompetenzeRichieste()).thenReturn("Competenze richieste");
        when(missioneDTO.getRequisitiExtra()).thenReturn("Requisiti extra");
        when(missioneDTO.getFoto()).thenReturn(file);
        when(missioneDTO.getCreatore()).thenReturn(utente);

        when(storageService.acceptUpload(file)).thenReturn(fileName);
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));
        when(missioneRepository.save(any(Missione.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var missione = gestioneMissione.registerMissione(missioneDTO);

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
    void acceptMissione() {
        var id = 1L;
        var missione = mock(Missione.class);

        when(missione.getStato()).thenReturn(Missione.MissioneStato.PENDING);

        when(missioneRepository.findById(id)).thenReturn(Optional.of(missione));

        gestioneMissione.acceptMissione(id);
        verify(missione).accettaMissione();
    }

    @Test
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
    void acceptMissioneNotExists() {

        var id = 1L;

        when(missioneRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> gestioneMissione.acceptMissione(id));
    }

}
