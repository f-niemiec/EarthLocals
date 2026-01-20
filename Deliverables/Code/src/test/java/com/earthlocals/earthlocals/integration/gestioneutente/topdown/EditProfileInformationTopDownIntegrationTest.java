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
public class EditProfileInformationTopDownIntegrationTest {

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
    public void TC4_1ModificaProfiloConSuccesso() {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Italia");


        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("volunteer@earthlocals.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
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
        driver.manage().window().setSize(new Dimension(1908, 1023));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("volunteer@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();


        driver.findElement(By.cssSelector("a[href=\"/account/edit\"]")).click();

        driver.findElement(By.id("firstNameEditForm")).clear();
        driver.findElement(By.id("firstNameEditForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameEditForm")).clear();
        driver.findElement(By.id("lastNameEditForm")).sendKeys("Squiteri");
        driver.findElement(By.id("nazionalitaEditForm")).sendKeys("1");
        driver.findElement(By.id("dataNascitaEditForm")).clear();
        driver.findElement(By.id("dataNascitaEditForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("sessoEditForm")).sendKeys("M");
        driver.findElement(By.cssSelector(".btn-primary")).click();
        assertEquals(driver.findElement(By.cssSelector(".alert.alert-success")).getText(), "Modifica profilo effettuata con successo");
    }

    @Test
    public void TC4_2ModificaProfiloNonAutenticato() throws InterruptedException {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Italia");


        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("volunteer@earthlocals.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
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
        driver.manage().window().setSize(new Dimension(1908, 1023));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("volunteer@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();


        driver.findElement(By.cssSelector("a[href=\"/account/edit\"]")).click();

        driver.manage().deleteAllCookies();

        driver.findElement(By.id("firstNameEditForm")).clear();
        driver.findElement(By.id("firstNameEditForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameEditForm")).clear();
        driver.findElement(By.id("lastNameEditForm")).sendKeys("Squiteri");
        driver.findElement(By.id("nazionalitaEditForm")).sendKeys("1");
        driver.findElement(By.id("dataNascitaEditForm")).clear();
        driver.findElement(By.id("dataNascitaEditForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("sessoEditForm")).sendKeys("M");
        driver.findElement(By.cssSelector(".btn-primary")).click();
        assertEquals("403 - Accesso negato!", driver.findElement(By.cssSelector("h2")).getText());
    }

    @Test
    public void TC4_3ModificaProfiloFormatoNomeNonRispettato() {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Italia");


        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("volunteer@earthlocals.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
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
        driver.manage().window().setSize(new Dimension(1908, 1023));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("volunteer@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();


        driver.findElement(By.cssSelector("a[href=\"/account/edit\"]")).click();

        driver.findElement(By.id("firstNameEditForm")).clear();
        driver.findElement(By.id("firstNameEditForm")).sendKeys(" ");
        driver.findElement(By.id("lastNameEditForm")).clear();
        driver.findElement(By.id("lastNameEditForm")).sendKeys("Squiteri");
        driver.findElement(By.id("nazionalitaEditForm")).sendKeys("1");
        driver.findElement(By.id("dataNascitaEditForm")).clear();
        driver.findElement(By.id("dataNascitaEditForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("sessoEditForm")).sendKeys("M");
        driver.findElement(By.cssSelector(".btn-primary")).click();

        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "Il nome è obbligatorio");
    }

    @Test
    public void TC4_4ModificaProfiloFormatoCognomeNonRispettato() {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Italia");


        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("volunteer@earthlocals.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
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
        driver.manage().window().setSize(new Dimension(1908, 1023));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("volunteer@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();


        driver.findElement(By.cssSelector("a[href=\"/account/edit\"]")).click();

        driver.findElement(By.id("firstNameEditForm")).clear();
        driver.findElement(By.id("firstNameEditForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameEditForm")).clear();
        driver.findElement(By.id("lastNameEditForm")).sendKeys(" ");
        driver.findElement(By.id("nazionalitaEditForm")).sendKeys("1");
        driver.findElement(By.id("dataNascitaEditForm")).clear();
        driver.findElement(By.id("dataNascitaEditForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("sessoEditForm")).sendKeys("M");
        driver.findElement(By.cssSelector(".btn-primary")).click();

        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "Il cognome è obbligatorio");
    }

    @Test
    public void TC4_5ModificaProfiloFormatoDataDiNascitaNonRispettato() {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Italia");


        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("volunteer@earthlocals.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
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
        driver.manage().window().setSize(new Dimension(1908, 1023));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("volunteer@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();


        driver.findElement(By.cssSelector("a[href=\"/account/edit\"]")).click();

        driver.findElement(By.id("firstNameEditForm")).clear();
        driver.findElement(By.id("firstNameEditForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameEditForm")).clear();
        driver.findElement(By.id("lastNameEditForm")).sendKeys("Squiteri");
        driver.findElement(By.id("nazionalitaEditForm")).sendKeys("1");
        driver.findElement(By.id("dataNascitaEditForm")).clear();
        driver.findElement(By.id("dataNascitaEditForm")).sendKeys("2077-04-01");
        driver.findElement(By.id("sessoEditForm")).sendKeys("M");
        driver.findElement(By.cssSelector(".btn-primary")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "La data di nascita inserita non è valida");
    }


}
