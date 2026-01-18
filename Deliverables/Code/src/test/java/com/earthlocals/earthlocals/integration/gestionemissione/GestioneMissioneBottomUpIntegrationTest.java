package com.earthlocals.earthlocals.integration.gestionemissione;

import com.earthlocals.earthlocals.config.SystemTestAppConfig;
import com.earthlocals.earthlocals.config.TestcontainerConfig;
import com.earthlocals.earthlocals.model.*;
import com.earthlocals.earthlocals.service.gestionemissioni.GestioneMissione;
import com.earthlocals.earthlocals.service.gestionemissioni.dto.MissioneDTO;
import com.earthlocals.earthlocals.service.gestionemissioni.exceptions.MissioneNotAcceptableException;
import com.earthlocals.earthlocals.service.gestionemissioni.exceptions.MissioneNotFoundException;
import com.earthlocals.earthlocals.service.gestionemissioni.pictures.PicturesFilesystemStorage;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

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
    @MockitoSpyBean
    private RuoloRepository ruoloRepository;

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

    private Missione validMissioneEntity() throws Exception{
        var paese = paeseRepository.findAll().getFirst();
        var missione = new Missione("Help teaching a Pechino ", paese,
                "Beijing", "Descrizione di almeno 20 caratteri", LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(3), "Competenze richieste",
                "Requisiti Extra", "static/resources/files/sample.png",
                new TreeSet<Candidatura>(),
                utenteRepository.findAll().iterator().next());

        missione.setCreatore(utenteRepository.findAll().iterator().next());
        missioneRepository.save(missione);
        return missione;
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

    @Test
    @WithAnonymousUser
    void registerMissioneAnonymousFails() throws Exception{
        var missioneDTO = validMissioneDTO();

        assertThrows(AuthorizationDeniedException.class, () -> gestioneMissione.registerMissione(missioneDTO));
        verify(missioneRepository, never()).save(any(Missione.class));
    }

    //Test incerto, potrebbero volercene uno per ogni constraint
    @Test
    @WithMockUser(roles = "ORGANIZER")
    void registerMissioneValidationFails() throws Exception {
        var missioneDTO = validMissioneDTO();
        missioneDTO.setCitta(null);

        assertThrows(ConstraintViolationException.class, () -> gestioneMissione.registerMissione(missioneDTO));
        verify(validator).validate(missioneDTO);
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void acceptMissione() throws Exception{
        Missione missione = validMissioneEntity();
        assertEquals(missione.getStato(), Missione.MissioneStato.PENDING);
        Long id = missione.getId();

        assertDoesNotThrow(() -> gestioneMissione.acceptMissione(id));
        Missione updated = missioneRepository.findById(id).orElseThrow();
        assertEquals(Missione.MissioneStato.ACCETTATA, updated.getStato());
    }

    @Test
    @WithMockUser(roles = {"VOLUNTEER", "ORGANIZER", "ACCOUNT_MANAGER"})
    void acceptMissioneNotModeratorFails() throws Exception{
        Missione missione = validMissioneEntity();
        Long id = missione.getId();
        assertEquals(Missione.MissioneStato.PENDING, missione.getStato());

        assertThrows(AuthorizationDeniedException.class, () -> gestioneMissione.acceptMissione(id));
        Missione fromDb = missioneRepository.findById(id).orElseThrow();
        assertEquals(Missione.MissioneStato.PENDING, fromDb.getStato());
    }

    @Test
    @WithAnonymousUser
    void acceptMissioneAnonymousFails() throws Exception{
        Missione missione = validMissioneEntity();
        Long id = missione.getId();
        assertEquals(Missione.MissioneStato.PENDING, missione.getStato());

        assertThrows(AuthorizationDeniedException.class, () -> gestioneMissione.acceptMissione(id));
        Missione fromDb = missioneRepository.findById(id).orElseThrow();
        assertEquals(Missione.MissioneStato.PENDING, fromDb.getStato());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void acceptMissioneNotPending() throws Exception{
        Missione.InternalMissioneStato internalStato = Missione.InternalMissioneStato.RIFIUTATA;
        Missione missione = validMissioneEntity();
        missione.forceInternalStatoForTest(internalStato);
        missioneRepository.saveAndFlush(missione);
        Long id = missione.getId();
        assertThrows(MissioneNotAcceptableException.class, () -> gestioneMissione.acceptMissione(id));
        Missione fromDb = missioneRepository.findById(id).orElseThrow();
        assertEquals(internalStato, fromDb.supplyStato());
    }

    //Non particolarmente sicuro
    @Test
    @WithMockUser(roles = "MODERATOR")
    void acceptMissioneNotExists() {
        Long id = 1L;
        assertThrows(MissioneNotFoundException.class, () -> gestioneMissione.acceptMissione(id));
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void rejectMissione() throws Exception{
        Missione missione = validMissioneEntity();
        Long id = missione.getId();

        assertDoesNotThrow(() -> gestioneMissione.rejectMissione(id));
        Missione fromDb = missioneRepository.findById(id).orElseThrow();
        assertEquals(Missione.MissioneStato.RIFIUTATA, fromDb.getStato());
    }

    @Test
    @WithMockUser(roles = {"VOLUNTEER", "ORGANIZER", "ACCOUNT_MANAGER"})
    void rejectMissioneNotModeratorFails() throws Exception{
        Missione missione = validMissioneEntity();
        Long id = missione.getId();
        assertEquals(Missione.MissioneStato.PENDING, missione.getStato());

        assertThrows(AuthorizationDeniedException.class, () -> gestioneMissione.rejectMissione(id));
        Missione fromDb = missioneRepository.findById(id).orElseThrow();
        assertEquals(Missione.MissioneStato.PENDING, fromDb.getStato());
    }

    @Test
    @WithAnonymousUser
    void rejectMissioneAnonymousFails() throws Exception{
        Missione missione = validMissioneEntity();
        Long id = missione.getId();
        assertEquals(Missione.MissioneStato.PENDING, missione.getStato());

        assertThrows(AuthorizationDeniedException.class, () -> gestioneMissione.rejectMissione(id));
        Missione fromDb = missioneRepository.findById(id).orElseThrow();
        assertEquals(Missione.MissioneStato.PENDING, fromDb.getStato());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void rejectMissioneNotPending() throws Exception{
        Missione.InternalMissioneStato internalStato = Missione.InternalMissioneStato.ACCETTATA;
        Missione missione = validMissioneEntity();
        missione.forceInternalStatoForTest(internalStato);
        missioneRepository.saveAndFlush(missione);
        Long id = missione.getId();
        assertThrows(MissioneNotAcceptableException.class, () -> gestioneMissione.rejectMissione(id));
        Missione fromDb = missioneRepository.findById(id).orElseThrow();
        assertEquals(internalStato, fromDb.supplyStato());
    }

    //Stesso discorso di acceptMissioneNotExists
    @Test
    @WithMockUser(roles = "MODERATOR")
    void rejectMissioneNotExists() {
        Long id = 1L;
        assertThrows(MissioneNotFoundException.class, () -> gestioneMissione.rejectMissione(id));
    }

    @Test
    void getMissioniAperte() throws Exception {
        Missione missione = validMissioneEntity();
        Page<Missione> res = gestioneMissione.getMissioniAperte(paeseRepository.findAll().getLast().getId(), 0, 10);
        assertTrue(res.getContent().stream()
                .allMatch(m -> m.getPaese().getId().equals(paeseRepository.findAll().getLast().getId())
                        && m.getStato() == Missione.MissioneStato.PENDING &&
                        m.getDataFine().isAfter(LocalDate.now())));
    }

    // Commento perchè non sono assolutamente convinto
    @Test
    void getMissioniApertePaeseNotFound() throws Exception {
        Missione missione = validMissioneEntity();
        Page<Missione> res = gestioneMissione.getMissioniAperte(2222, 0, 10);
        assertFalse(res.getContent().stream()
                .allMatch(m -> m.getPaese().getId().equals(paeseRepository.findAll().getLast().getId())
                        && m.getStato() == Missione.MissioneStato.PENDING &&
                        m.getDataFine().isAfter(LocalDate.now())));
    }

    @Test
    void getMissioniApertePaeseNull() throws Exception {
        Missione missione1 = validMissioneEntity();
        missione1.forceInternalStatoForTest(Missione.InternalMissioneStato.PENDING);
        missione1.setDataFine(LocalDate.now().plusDays(10));
        missioneRepository.saveAndFlush(missione1);
        Missione missione2 = validMissioneEntity();
        missione2.forceInternalStatoForTest(Missione.InternalMissioneStato.PENDING);
        missione2.setDataFine(LocalDate.now().plusDays(5));
        missioneRepository.saveAndFlush(missione2);
        Page<Missione> res = gestioneMissione.getMissioniAperte(null, 0, 10);
        assertTrue(res.getTotalElements() >= 2);
    }

    //Again, non sono assolutamente convinto
    @Test
    void getMissioniApertePaeseZero() throws Exception{
        Paese paese = paeseRepository.findAll().get(0);
        Missione missione1 = validMissioneEntity();
        missione1.setPaese(paese);
        missione1.forceInternalStatoForTest(Missione.InternalMissioneStato.PENDING);
        missione1.setDataFine(LocalDate.now().plusDays(10));
        missioneRepository.saveAndFlush(missione1);
        Missione missione2 = validMissioneEntity();
        missione2.setPaese(paese);
        missione2.forceInternalStatoForTest(Missione.InternalMissioneStato.PENDING);
        missione2.setDataFine(LocalDate.now().plusDays(5));
        missioneRepository.saveAndFlush(missione2);
        Page<Missione> res = gestioneMissione.getMissioniAperte(0, 0, 10);
        assertTrue(res.getTotalElements() >= 2);
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
        Page<Missione> res = gestioneMissione.getMissioniPending(0, 10);
        assertNotNull(res);
        assertTrue(res.getContent().stream().allMatch(m -> m.getStato() == Missione.MissioneStato.PENDING));
    }

    @Test
    @WithMockUser(roles = {"VOLUNTEER", "ORGANIZER", "ACCOUNT_MANAGER"})
    void getMissioniPendingNotModeratorFails() {
        assertThrows(AuthorizationDeniedException.class, () -> gestioneMissione.getMissioniPending(0, 10));
    }

    @Test
    @WithAnonymousUser
    void getMissioniPendingAnonymousFails() {
        assertThrows(AuthorizationDeniedException.class, () -> gestioneMissione.getMissioniPending(0, 10));
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
    void getMissioniOrganizzatore() throws Exception{
        Ruolo organizerRole = new Ruolo(Ruolo.ORGANIZER);
        ruoloRepository.save(organizerRole);
        Utente organizer = Utente.utenteBuilder()
                .nome("Mario")
                .cognome("Rossi")
                .email("organizer@test.com")
                .password("password")
                .dataNascita(LocalDate.of(1990, 1, 1))
                .sesso('M')
                .pending(false)
                .ruoli(Set.of(organizerRole))
                .build();
        utenteRepository.save(organizer);
        Missione missione1 = validMissioneEntity();
        missione1.setCreatore(organizer);
        missione1.forceInternalStatoForTest(Missione.InternalMissioneStato.PENDING);
        missioneRepository.saveAndFlush(missione1);
        Missione missione2 = validMissioneEntity();
        missione2.setCreatore(organizer);
        missione2.forceInternalStatoForTest(Missione.InternalMissioneStato.PENDING);
        missioneRepository.saveAndFlush(missione2);
        var auth = new TestingAuthenticationToken(organizer, null, "ROLE_ORGANIZER");
        SecurityContextHolder.getContext().setAuthentication(auth);
        Page<Missione> res = gestioneMissione.getMissioniOrganizzatore(0, 10);
        List<Long> resIds = res.map(Missione::getId).getContent();
        assertTrue(resIds.contains(missione1.getId()));
        assertTrue(resIds.contains(missione2.getId()));
    }

    @Test
    @WithMockUser(roles = {"VOLUNTEER", "MODERATOR", "ACCOUNT_MANAGER"})
    void getMissioniOrganizzatoreNotOrganizerFails() {
        assertThrows(AuthorizationDeniedException.class, () -> gestioneMissione.getMissioniOrganizzatore(0, 1));
    }

    @Test
    @WithAnonymousUser
    void getMissioniOrganizzatoreAnonymousFails() {
        assertThrows(AuthorizationDeniedException.class, () -> gestioneMissione.getMissioniOrganizzatore(0, 1));
    }

    @Test
    void getMissioneById() throws Exception {
        Missione missione = validMissioneEntity();
        Long id = missione.getId();
        Missione res = assertDoesNotThrow(() -> gestioneMissione.getMissioneById(id));
        assertNotNull(res);
        assertEquals(missione.getId(), res.getId());
        assertEquals(missione.getNome(), res.getNome());
        assertEquals(missione.getCreatore(), res.getCreatore());
    }

    @Test
    void getMissioneByIdNotFoundIntegration() {
        Long nonExistentId = 9999L;
        assertThrows(MissioneNotFoundException.class, () -> gestioneMissione.getMissioneById(nonExistentId));
    }












}
