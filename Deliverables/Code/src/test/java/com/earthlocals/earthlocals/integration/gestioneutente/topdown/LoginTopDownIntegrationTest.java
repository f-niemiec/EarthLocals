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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SystemTestAppConfig.class, TestcontainerConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LoginTopDownIntegrationTest {

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
    public void TC3_1LoginSuccessfull() {
        var utente = Mockito.mock(Utente.class, Mockito.RETURNS_SMART_NULLS);
        Mockito.when(utente.getEmail()).thenReturn("andrea.squitieri@mail.com");
        Mockito.when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
        Mockito.when(utente.isEnabled()).thenReturn(true);
        Mockito.when(utente.isAccountNonExpired()).thenReturn(true);
        Mockito.when(utente.isAccountNonLocked()).thenReturn(true);
        Mockito.when(utente.isCredentialsNonExpired()).thenReturn(true);

        Mockito.when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1280, 672));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("andrea.squitieri@mail.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector("a[href=\"/account/edit\"]")).getText(), "Profilo");
    }

    @Test
    public void TC3_2EmailNonPresente() {
        var utente = Mockito.mock(Utente.class, Mockito.RETURNS_SMART_NULLS);
        Mockito.when(utente.getEmail()).thenReturn("andrea.squitieri@mail.com");
        Mockito.when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
        Mockito.when(utente.isEnabled()).thenReturn(true);
        Mockito.when(utente.isAccountNonExpired()).thenReturn(true);
        Mockito.when(utente.isAccountNonLocked()).thenReturn(true);
        Mockito.when(utente.isCredentialsNonExpired()).thenReturn(true);

        Mockito.when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(null);


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1280, 672));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("andrea.squitieri@mail.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();

        assertEquals(driver.findElement(By.cssSelector(".alert")).getText(), "E-mail o password non valida");
    }

    @Test
    public void TC3_3PasswordErrata() {
        var utente = Mockito.mock(Utente.class, Mockito.RETURNS_SMART_NULLS);
        Mockito.when(utente.getEmail()).thenReturn("andrea.squitieri@mail.com");
        Mockito.when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
        Mockito.when(utente.isEnabled()).thenReturn(true);
        Mockito.when(utente.isAccountNonExpired()).thenReturn(true);
        Mockito.when(utente.isAccountNonLocked()).thenReturn(true);
        Mockito.when(utente.isCredentialsNonExpired()).thenReturn(true);

        Mockito.when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1280, 672));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("andrea.squitieri@mail.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("pwnnsicura");
        driver.findElement(By.cssSelector(".btn")).click();

        assertEquals(driver.findElement(By.cssSelector(".alert")).getText(), "E-mail o password non valida");
    }


}
