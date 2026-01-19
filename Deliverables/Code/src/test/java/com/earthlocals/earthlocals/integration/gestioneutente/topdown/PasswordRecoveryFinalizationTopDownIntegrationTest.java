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
import org.openqa.selenium.By;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SystemTestAppConfig.class, TestcontainerConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PasswordRecoveryFinalizationTopDownIntegrationTest {

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
    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public void TC7_1RecuperoPasswordConSuccesso() throws InterruptedException {
        var utente = mock(Utente.class, RETURNS_SMART_NULLS);
        when(utente.getEmail()).thenReturn("john.travolta42@email.it");
        when(utenteRepository.findByEmail(utente.getEmail())).thenReturn(utente);

        var resetToken = mock(PasswordResetToken.class, RETURNS_SMART_NULLS);
        when(resetToken.getToken()).thenReturn("token");
        when(resetToken.getUtente()).thenReturn(utente);

        when(passwordResetTokenRepository.findByToken(resetToken.getToken())).thenReturn(Optional.of(resetToken));

        driver.get(LocalTestWebServer.obtain(this.context).uri("/resetPasswordConfirm?token=" + resetToken.getToken()));
        driver.findElement(By.id("passwordReset")).sendKeys("Lc67Jk!d");
        driver.findElement(By.id("matchingPasswordReset")).sendKeys("Lc67Jk!d");
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".alert.alert-success")).getText(), "Password cambiata con successo");

    }

    @Test
    public void TC7_2RecuperoPasswordTokenNonValido() throws InterruptedException {
        var utente = mock(Utente.class, RETURNS_SMART_NULLS);
        when(utente.getEmail()).thenReturn("john.travolta42@email.it");
        when(utenteRepository.findByEmail(utente.getEmail())).thenReturn(utente);

        var resetToken = mock(PasswordResetToken.class, RETURNS_SMART_NULLS);
        when(resetToken.getToken()).thenReturn("token");
        when(resetToken.getUtente()).thenReturn(utente);

        when(passwordResetTokenRepository.findByToken(resetToken.getToken())).thenReturn(Optional.empty());

        driver.get(LocalTestWebServer.obtain(this.context).uri("/resetPasswordConfirm?token=" + resetToken.getToken()));
        driver.findElement(By.id("passwordReset")).sendKeys("Lc67Jk!d");
        driver.findElement(By.id("matchingPasswordReset")).sendKeys("Lc67Jk!d");
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".alert.alert-danger")).getText(), "Il token di reset non è valido");

    }

    @Test
    public void TC7_3RecuperoPasswordTokenScaduto() throws InterruptedException {
        var utente = mock(Utente.class, RETURNS_SMART_NULLS);
        when(utente.getEmail()).thenReturn("john.travolta42@email.it");
        when(utenteRepository.findByEmail(utente.getEmail())).thenReturn(utente);

        var resetToken = mock(PasswordResetToken.class, RETURNS_SMART_NULLS);
        when(resetToken.getToken()).thenReturn("token");
        when(resetToken.getExpiryDate()).thenReturn(LocalDateTime.of(2020, 1, 1, 0, 0));
        when(resetToken.isExpired()).thenReturn(true);
        when(resetToken.getUtente()).thenReturn(utente);

        when(passwordResetTokenRepository.findByToken(resetToken.getToken())).thenReturn(Optional.of(resetToken));

        driver.get(LocalTestWebServer.obtain(this.context).uri("/resetPasswordConfirm?token=" + resetToken.getToken()));
        driver.findElement(By.id("passwordReset")).sendKeys("Lc67Jk!d");
        driver.findElement(By.id("matchingPasswordReset")).sendKeys("Lc67Jk!d");
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".alert.alert-danger")).getText(), "Il token di reset è scaduto");

    }

    @Test
    public void TC7_4RecuperoPasswordNoSpeciale() throws InterruptedException {
        var utente = mock(Utente.class, RETURNS_SMART_NULLS);
        when(utente.getEmail()).thenReturn("john.travolta42@email.it");
        when(utenteRepository.findByEmail(utente.getEmail())).thenReturn(utente);

        var resetToken = mock(PasswordResetToken.class, RETURNS_SMART_NULLS);
        when(resetToken.getToken()).thenReturn("token");
        when(resetToken.getUtente()).thenReturn(utente);

        when(passwordResetTokenRepository.findByToken(resetToken.getToken())).thenReturn(Optional.of(resetToken));

        driver.get(LocalTestWebServer.obtain(this.context).uri("/resetPasswordConfirm?token=" + resetToken.getToken()));
        driver.findElement(By.id("passwordReset")).sendKeys("Lc67Jk1d");
        driver.findElement(By.id("matchingPasswordReset")).sendKeys("Lc67Jk1d");
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "La nuova password deve contenere almeno un carattere speciale");

    }

    @Test
    public void TC7_5RecuperoPasswordNoMinuscolo() throws InterruptedException {
        var utente = mock(Utente.class, RETURNS_SMART_NULLS);
        when(utente.getEmail()).thenReturn("john.travolta42@email.it");
        when(utenteRepository.findByEmail(utente.getEmail())).thenReturn(utente);

        var resetToken = mock(PasswordResetToken.class, RETURNS_SMART_NULLS);
        when(resetToken.getToken()).thenReturn("token");
        when(resetToken.getUtente()).thenReturn(utente);

        when(passwordResetTokenRepository.findByToken(resetToken.getToken())).thenReturn(Optional.of(resetToken));

        driver.get(LocalTestWebServer.obtain(this.context).uri("/resetPasswordConfirm?token=" + resetToken.getToken()));
        driver.findElement(By.id("passwordReset")).sendKeys("LC67JK!D");
        driver.findElement(By.id("matchingPasswordReset")).sendKeys("LC67JK!D");
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "La nuova password deve contenere almeno un carattere minuscolo");
    }

    @Test
    public void TC7_6RecuperoPasswordNoMaiuscolo() throws InterruptedException {
        var utente = mock(Utente.class, RETURNS_SMART_NULLS);
        when(utente.getEmail()).thenReturn("john.travolta42@email.it");
        when(utenteRepository.findByEmail(utente.getEmail())).thenReturn(utente);

        var resetToken = mock(PasswordResetToken.class, RETURNS_SMART_NULLS);
        when(resetToken.getToken()).thenReturn("token");
        when(resetToken.getUtente()).thenReturn(utente);

        when(passwordResetTokenRepository.findByToken(resetToken.getToken())).thenReturn(Optional.of(resetToken));

        driver.get(LocalTestWebServer.obtain(this.context).uri("/resetPasswordConfirm?token=" + resetToken.getToken()));
        driver.findElement(By.id("passwordReset")).sendKeys("lc67jk!d");
        driver.findElement(By.id("matchingPasswordReset")).sendKeys("lc67jk!d");
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "La nuova password deve contenere almeno un carattere maiuscolo");
    }

    @Test
    public void TC7_7RecuperoPasswordNoNumero() throws InterruptedException {
        var utente = mock(Utente.class, RETURNS_SMART_NULLS);
        when(utente.getEmail()).thenReturn("john.travolta42@email.it");
        when(utenteRepository.findByEmail(utente.getEmail())).thenReturn(utente);

        var resetToken = mock(PasswordResetToken.class, RETURNS_SMART_NULLS);
        when(resetToken.getToken()).thenReturn("token");
        when(resetToken.getUtente()).thenReturn(utente);

        when(passwordResetTokenRepository.findByToken(resetToken.getToken())).thenReturn(Optional.of(resetToken));

        driver.get(LocalTestWebServer.obtain(this.context).uri("/resetPasswordConfirm?token=" + resetToken.getToken()));
        driver.findElement(By.id("passwordReset")).sendKeys("LcdTJk!d");
        driver.findElement(By.id("matchingPasswordReset")).sendKeys("LcdTJk!d");
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "La nuova password deve contenere almeno un numero");

    }

    @Test
    public void TC7_8RecuperoPasswordNoLunghezza() throws InterruptedException {
        var utente = mock(Utente.class, RETURNS_SMART_NULLS);
        when(utente.getEmail()).thenReturn("john.travolta42@email.it");
        when(utenteRepository.findByEmail(utente.getEmail())).thenReturn(utente);

        var resetToken = mock(PasswordResetToken.class, RETURNS_SMART_NULLS);
        when(resetToken.getToken()).thenReturn("token");
        when(resetToken.getUtente()).thenReturn(utente);

        when(passwordResetTokenRepository.findByToken(resetToken.getToken())).thenReturn(Optional.of(resetToken));

        driver.get(LocalTestWebServer.obtain(this.context).uri("/resetPasswordConfirm?token=" + resetToken.getToken()));
        driver.findElement(By.id("passwordReset")).sendKeys("Lc67Jk!");
        driver.findElement(By.id("matchingPasswordReset")).sendKeys("Lc67Jk!");
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "La nuova password deve essere lunga almeno 8 caratteri");

    }

    @Test
    public void TC7_9RecuperoPasswordNonCoincidono() throws InterruptedException {
        var utente = mock(Utente.class, RETURNS_SMART_NULLS);
        when(utente.getEmail()).thenReturn("john.travolta42@email.it");
        when(utenteRepository.findByEmail(utente.getEmail())).thenReturn(utente);

        var resetToken = mock(PasswordResetToken.class, RETURNS_SMART_NULLS);
        when(resetToken.getToken()).thenReturn("token");
        when(resetToken.getUtente()).thenReturn(utente);

        when(passwordResetTokenRepository.findByToken(resetToken.getToken())).thenReturn(Optional.of(resetToken));

        driver.get(LocalTestWebServer.obtain(this.context).uri("/resetPasswordConfirm?token=" + resetToken.getToken()));
        driver.findElement(By.id("passwordReset")).sendKeys("Lc67Jk!d");
        driver.findElement(By.id("matchingPasswordReset")).sendKeys("Password1!");
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "Le password non coincidono");

    }


}
