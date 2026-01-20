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
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SystemTestAppConfig.class, TestcontainerConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class EditPasswordTopDownIntegrationTest {

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
    public void TC5_1PasswordModificataConSuccesso() throws InterruptedException {
        var oldPassword = "mGdk02@L";
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Italia");

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("mario.rossi@mariorossi.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode(oldPassword));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);

        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("mario.rossi@mariorossi.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys(oldPassword);
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.cssSelector("a[href=\"/account/edit\"]")).click();
        driver.findElement(By.cssSelector("a[href=\"/account/edit-password\"]")).click();
        driver.findElement(By.id("currentPasswordEditPasswordForm")).sendKeys(oldPassword);
        driver.findElement(By.id("newPasswordEditPasswordForm")).sendKeys("Lc67Jk!d");
        driver.findElement(By.id("matchingPasswordEditPasswordForm")).sendKeys("Lc67Jk!d");
        driver.findElement(By.cssSelector("#editPasswordForm button.btn[type=submit]")).click();
        assertEquals(driver.findElement(By.cssSelector(".alert.alert-success")).getText(), "Cambio password effettuato con successo! :]");
    }

    @Test
    public void TC5_2ModificaPasswordUtenteNonAutenticato() throws InterruptedException {
        var oldPassword = "mGdk02@L";
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Italia");

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("mario.rossi@mariorossi.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode(oldPassword));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);

        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("mario.rossi@mariorossi.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys(oldPassword);
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.cssSelector("a[href=\"/account/edit\"]")).click();
        driver.findElement(By.cssSelector("a[href=\"/account/edit-password\"]")).click();

        driver.manage().deleteAllCookies();

        driver.findElement(By.id("currentPasswordEditPasswordForm")).sendKeys(oldPassword);
        driver.findElement(By.id("newPasswordEditPasswordForm")).sendKeys("Lc67Jk!d");
        driver.findElement(By.id("matchingPasswordEditPasswordForm")).sendKeys("Lc67Jk!d");
        driver.findElement(By.cssSelector("#editPasswordForm button.btn[type=submit]")).click();

        assertEquals(driver.findElement(By.cssSelector(".col-md-12 > p")).getText(), "Ci dispiace, non sei autorizzato a vedere questa risorsa.");
    }

    @Test
    public void TC5_3ModificaPasswordVecchiaPasswordErrata() throws InterruptedException {
        var oldPassword = "mGdk02@L";
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Italia");

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("mario.rossi@mariorossi.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode(oldPassword));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);

        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("mario.rossi@mariorossi.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys(oldPassword);
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.cssSelector("a[href=\"/account/edit\"]")).click();
        driver.findElement(By.cssSelector("a[href=\"/account/edit-password\"]")).click();
        driver.findElement(By.id("currentPasswordEditPasswordForm")).sendKeys("mGdk02@1");
        driver.findElement(By.id("newPasswordEditPasswordForm")).sendKeys("Lc67Jk!d");
        driver.findElement(By.id("matchingPasswordEditPasswordForm")).sendKeys("Lc67Jk!d");
        driver.findElement(By.cssSelector("#editPasswordForm button.btn[type=submit]")).click();

        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "Password errata.");
    }

    @Test
    public void TC5_4ModificaPasswordCarattereSpecialeMancante() throws InterruptedException {
        var oldPassword = "mGdk02@L";
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Italia");

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("mario.rossi@mariorossi.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode(oldPassword));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);

        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("mario.rossi@mariorossi.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys(oldPassword);
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.cssSelector("a[href=\"/account/edit\"]")).click();
        driver.findElement(By.cssSelector("a[href=\"/account/edit-password\"]")).click();
        driver.findElement(By.id("currentPasswordEditPasswordForm")).sendKeys(oldPassword);
        driver.findElement(By.id("newPasswordEditPasswordForm")).sendKeys("Lc67Jk1d");
        driver.findElement(By.id("matchingPasswordEditPasswordForm")).sendKeys("Lc67Jk1d");
        driver.findElement(By.cssSelector("#editPasswordForm button.btn[type=submit]")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "La nuova password deve contenere almeno un carattere speciale");
    }

    @Test
    public void TC5_5ModificaPasswordCarattereMinuscoloMancante() throws InterruptedException {
        var oldPassword = "mGdk02@L";
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Italia");

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("mario.rossi@mariorossi.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode(oldPassword));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);

        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("mario.rossi@mariorossi.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys(oldPassword);
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.cssSelector("a[href=\"/account/edit\"]")).click();
        driver.findElement(By.cssSelector("a[href=\"/account/edit-password\"]")).click();
        driver.findElement(By.id("currentPasswordEditPasswordForm")).sendKeys(oldPassword);
        driver.findElement(By.id("newPasswordEditPasswordForm")).sendKeys("LC67JK!D");
        driver.findElement(By.id("matchingPasswordEditPasswordForm")).sendKeys("LC67JK!D");
        driver.findElement(By.cssSelector("#editPasswordForm button.btn[type=submit]")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "La nuova password deve contenere almeno un carattere minuscolo");
    }

    @Test
    public void TC5_6ModificaPasswordCarattereMaiuscoloMancante() throws InterruptedException {
        var oldPassword = "mGdk02@L";
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Italia");

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("mario.rossi@mariorossi.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode(oldPassword));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);

        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("mario.rossi@mariorossi.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys(oldPassword);
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.cssSelector("a[href=\"/account/edit\"]")).click();
        driver.findElement(By.cssSelector("a[href=\"/account/edit-password\"]")).click();
        driver.findElement(By.id("currentPasswordEditPasswordForm")).sendKeys(oldPassword);
        driver.findElement(By.id("newPasswordEditPasswordForm")).sendKeys("lc67jk!d");
        driver.findElement(By.id("matchingPasswordEditPasswordForm")).sendKeys("lc67jk!d");
        driver.findElement(By.cssSelector("#editPasswordForm button.btn[type=submit]")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "La nuova password deve contenere almeno un carattere maiuscolo");
    }

    @Test
    public void TC5_7ModificaPasswordNumeroMancante() throws InterruptedException {
        var oldPassword = "mGdk02@L";
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Italia");

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("mario.rossi@mariorossi.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode(oldPassword));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);

        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("mario.rossi@mariorossi.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys(oldPassword);
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.cssSelector("a[href=\"/account/edit\"]")).click();
        driver.findElement(By.cssSelector("a[href=\"/account/edit-password\"]")).click();
        driver.findElement(By.id("currentPasswordEditPasswordForm")).sendKeys(oldPassword);
        driver.findElement(By.id("newPasswordEditPasswordForm")).sendKeys("LcdTJk!d");
        driver.findElement(By.id("matchingPasswordEditPasswordForm")).sendKeys("LcdTJk!d");
        driver.findElement(By.cssSelector("#editPasswordForm button.btn[type=submit]")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "La nuova password deve contenere almeno un numero");
    }

    @Test
    public void TC5_8ModificaPasswordTroppoCorta() throws InterruptedException {
        var oldPassword = "mGdk02@L";
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Italia");

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("mario.rossi@mariorossi.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode(oldPassword));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);

        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("mario.rossi@mariorossi.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys(oldPassword);
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.cssSelector("a[href=\"/account/edit\"]")).click();
        driver.findElement(By.cssSelector("a[href=\"/account/edit-password\"]")).click();
        driver.findElement(By.id("currentPasswordEditPasswordForm")).sendKeys(oldPassword);
        driver.findElement(By.id("newPasswordEditPasswordForm")).sendKeys("Lc67Jk!");
        driver.findElement(By.id("matchingPasswordEditPasswordForm")).sendKeys("Lc67Jk!");
        driver.findElement(By.cssSelector("#editPasswordForm button.btn[type=submit]")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "La nuova password deve essere lunga almeno 8 caratteri");
    }

    @Test
    public void TC5_9ModificaPasswordNuoveNonCoincidenti() throws InterruptedException {
        var oldPassword = "mGdk02@L";
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Italia");

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("mario.rossi@mariorossi.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode(oldPassword));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);

        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("mario.rossi@mariorossi.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys(oldPassword);
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.cssSelector("a[href=\"/account/edit\"]")).click();
        driver.findElement(By.cssSelector("a[href=\"/account/edit-password\"]")).click();
        driver.findElement(By.id("currentPasswordEditPasswordForm")).sendKeys(oldPassword);
        driver.findElement(By.id("newPasswordEditPasswordForm")).sendKeys("Lc67Jk!d");
        driver.findElement(By.id("matchingPasswordEditPasswordForm")).sendKeys("Lc67Jk?d");
        driver.findElement(By.cssSelector("#editPasswordForm button.btn[type=submit]")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "Le password non coincidono");
    }


}
