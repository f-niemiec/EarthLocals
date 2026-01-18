package com.earthlocals.earthlocals.integration.gestionemissione;

import com.earthlocals.earthlocals.config.SystemTestAppConfig;
import com.earthlocals.earthlocals.config.TestcontainerConfig;
import com.earthlocals.earthlocals.model.*;
import com.earthlocals.earthlocals.service.gestionemissioni.GestioneMissione;
import com.earthlocals.earthlocals.service.gestionemissioni.dto.MissioneDTO;
import com.earthlocals.earthlocals.service.gestionemissioni.pictures.PicturesFilesystemStorage;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ActiveProfiles("test")
@SpringBootTest
@ContextConfiguration(classes = {SystemTestAppConfig.class, TestcontainerConfig.class})
@Testcontainers
@Transactional
public class GestioneMissioneBottomUpIntegrationTest {
    private final Resource pictureResource = new ClassPathResource("static/resources/files/sample.png");
    @MockitoSpyBean
    private Validator validator;
    @MockitoSpyBean
    private MissioneRepository missioneRepository;
    @MockitoSpyBean
    private PicturesFilesystemStorage storageService;
    @MockitoSpyBean
    private PaeseRepository paeseRepository;
    @MockitoSpyBean
    private UtenteRepository utenteRepository;
    @MockitoSpyBean
    private GestioneMissione gestioneMissione;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    private MissioneDTO validMissioneDTO() throws Exception{
        var picture = new MockMultipartFile("sample.png", pictureResource.getInputStream());
        var nazioneId = 1;

        MissioneDTO missioneDTO = new MissioneDTO();
        missioneDTO.setCreatore(utenteRepository.findAll().iterator().next());
        missioneDTO.setDataInizio(LocalDate.now().plusDays(2));
        missioneDTO.setDataFine(LocalDate.now().plusDays(3));
        missioneDTO.setNome("Help teaching a Pechino ");
        missioneDTO.setPaese(nazioneId);
        missioneDTO.setCitta("Beijing");
        missioneDTO.setFoto(picture);
        missioneDTO.setDescrizione("Descrizione di almeno 20 caratteri");
        missioneDTO.setCompetenzeRichieste("Competenze richieste");
        missioneDTO.setRequisitiExtra("Requisiti extra");
        return missioneDTO;
    }


    @Test
    @WithMockUser(roles = "ORGANIZER")
    void registerMissione() throws Exception {
        var missioneDTO = validMissioneDTO();

        var res = assertDoesNotThrow(() -> gestioneMissione.registerMissione(missioneDTO));
        var missioneCaptor = ArgumentCaptor.forClass(Missione.class);
        verify(missioneRepository, times(1)).save(missioneCaptor.capture());
        var savedMissione = missioneCaptor.getValue();
        assertSame(savedMissione, res);
        assertNotNull(res);
        assertNotNull(res.getId());
        assertEquals(missioneDTO.getNome(), res.getNome());
        assertEquals(missioneDTO.getCitta(), res.getCitta());
        assertEquals(Missione.MissioneStato.PENDING, res.getStato());
        assertNotNull(res.getCreatore());
        assertNotNull(res.getPaese());
    }

    @Test
    @WithMockUser(roles = {"VOLUNTEER", "MODERATOR", "ACCOUNT_MANAGER"})
    void registerMissioneNotOrganizerFails() throws Exception{
        var missioneDTO = validMissioneDTO();

        assertThrows(AuthorizationDeniedException.class, () -> gestioneMissione.registerMissione(missioneDTO));
        verify(missioneRepository, never()).save(any(Missione.class));
    }


}
