package com.earthlocals.earthlocals.integration.gestioneutente.topdown;

import com.earthlocals.earthlocals.config.SystemTestAppConfig;
import com.earthlocals.earthlocals.config.TestcontainerConfig;
import com.earthlocals.earthlocals.model.*;
import com.earthlocals.earthlocals.service.gestioneemail.GestioneEmail;
import com.earthlocals.earthlocals.service.gestioneutente.passport.PassportStorageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.http.server.LocalTestWebServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SystemTestAppConfig.class, TestcontainerConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RegistrationConfirmationTopDownIntegrationTest {

    private final ClassPathResource file = new ClassPathResource("static/resources/files/sample.pdf");
    private final ClassPathResource webpFile = new ClassPathResource("static/resources/files/sample.webp");

    JavascriptExecutor js;
    @Autowired
    private ApplicationContext context;
    private WebDriver driver;
    private Map<String, Object> vars;
    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private CandidaturaRepository candidaturaRepository;
    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private MissioneRepository missioneRepository;
    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private PaeseRepository paeseRepository;
    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private RecensioneRepository recensioneRepository;
    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private RuoloRepository ruoloRepository;
    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private UtenteRepository utenteRepository;
    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private VerificationTokenRepository verificationTokenRepository;
    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private VolontarioRepository volontarioRepository;
    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private PassportStorageService passportStorageService;
    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private GestioneEmail gestioneEmail;

    @BeforeEach
    public void setUp(WebApplicationContext context) {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-headless");
        driver = new FirefoxDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

        js = (JavascriptExecutor) driver;
        vars = new HashMap<String, Object>();
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    private String getFilePath() throws IOException {
        return file.getFilePath().toAbsolutePath().toString();

    }

    private String getWebpFilePath() throws IOException {
        return webpFile.getFilePath().toAbsolutePath().toString();

    }

    @Test
    public void TC2_1ConfermaRegistrazioneConSuccesso() throws IOException {
        var utente = Mockito.mock(Utente.class, Mockito.RETURNS_SMART_NULLS);
        Mockito.when(utente.getPending()).thenReturn(true);
        Mockito.when(utente.getEmail()).thenReturn("mario.rossi@mariorossi.com");
        Mockito.when(utente.getNome()).thenReturn("Mario");
        Mockito.when(utenteRepository.findByEmail(utente.getEmail())).thenReturn(utente);

        var verificationToken = Mockito.mock(VerificationToken.class, Mockito.RETURNS_SMART_NULLS);
        Mockito.when(verificationToken.getUtente()).thenReturn(utente);
        Mockito.when(verificationToken.getToken()).thenReturn("token");
        Mockito.when(verificationToken.getExpiryDate()).thenReturn(LocalDateTime.of(2099, 10, 10, 0, 0));

        Mockito.when(verificationTokenRepository.findByToken(verificationToken.getToken())).thenReturn(Optional.of(verificationToken));

        driver.get(LocalTestWebServer.obtain(this.context).uri("/registration/registrationConfirm?token=" + verificationToken.getToken()));
        driver.manage().window().setSize(new Dimension(1280, 672));
        assertEquals(driver.findElement(By.cssSelector(".alert")).getText(), "Il tuo account è stato verificato con successo");
    }

    @Test
    public void TC2_2ConfermaRegistrazioneTokenNonPresente() throws IOException {
        var utente = Mockito.mock(Utente.class, Mockito.RETURNS_SMART_NULLS);
        Mockito.when(utente.getPending()).thenReturn(true);
        Mockito.when(utente.getEmail()).thenReturn("mario.rossi@mariorossi.com");
        Mockito.when(utente.getNome()).thenReturn("Mario");
        Mockito.when(utenteRepository.findByEmail(utente.getEmail())).thenReturn(utente);

        driver.get(LocalTestWebServer.obtain(this.context).uri("/registration/registrationConfirm?token=" + UUID.randomUUID()));
        driver.manage().window().setSize(new Dimension(1280, 672));
        assertEquals(driver.findElement(By.cssSelector(".alert.alert-danger")).getText(), "Il token di verifica non è valido");
    }


    @Test
    public void TC2_3ConfermaRegistrazioneTokenScaduto() throws IOException {
        var utente = Mockito.mock(Utente.class, Mockito.RETURNS_SMART_NULLS);
        Mockito.when(utente.getPending()).thenReturn(true);
        Mockito.when(utente.getEmail()).thenReturn("mario.rossi@mariorossi.com");
        Mockito.when(utente.getNome()).thenReturn("Mario");
        Mockito.when(utenteRepository.findByEmail(utente.getEmail())).thenReturn(utente);

        var verificationToken = Mockito.mock(VerificationToken.class, Mockito.RETURNS_SMART_NULLS);
        Mockito.when(verificationToken.getUtente()).thenReturn(utente);
        Mockito.when(verificationToken.getToken()).thenReturn("token");
        Mockito.when(verificationToken.getExpiryDate()).thenReturn(LocalDateTime.of(2026, 1, 15, 0, 0));

        Mockito.when(verificationTokenRepository.findByToken(verificationToken.getToken())).thenReturn(Optional.of(verificationToken));


        driver.get(LocalTestWebServer.obtain(this.context).uri("/registration/registrationConfirm?token=" + verificationToken.getToken()));

        driver.manage().window().setSize(new Dimension(1280, 672));
        assertEquals(driver.findElement(By.cssSelector(".alert.alert-danger")).getText(), "Il token di verifica non è valido");
    }


}
