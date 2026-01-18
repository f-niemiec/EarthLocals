package com.earthlocals.earthlocals.integration.gestionemissione.topdown;

import com.earthlocals.earthlocals.config.SystemTestAppConfig;
import com.earthlocals.earthlocals.config.TestcontainerConfig;
import com.earthlocals.earthlocals.model.*;
import com.earthlocals.earthlocals.service.gestioneemail.GestioneEmail;
import com.earthlocals.earthlocals.service.gestioneutente.passport.PassportStorageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.http.server.LocalTestWebServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SystemTestAppConfig.class, TestcontainerConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CreateMissioneTopDownIntegrationTest {

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
    public void TC8_1SalvataggioMissioneSuccesso() throws IOException {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("organizer1@earthlocals.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_ORGANIZER")));
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1534, 766));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("organizer1@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.linkText("Profilo")).click();
        driver.findElement(By.linkText("Nuova missione")).click();
        driver.findElement(By.id("passaportoEditPassportForm")).sendKeys(getWebpFilePath());
        driver.findElement(By.id("title")).sendKeys("Help Teaching a Pechino");
        driver.findElement(By.id("description")).sendKeys("Vieni ad insegnare l’inglese in un doposcuola nel quartiere Dongsi a ragazzi delle scuole medie");
        {
            WebElement dropdown = driver.findElement(By.id("paese"));
            dropdown.findElement(By.xpath("//option[. = 'Cina']")).click();
        }
        driver.findElement(By.id("citta")).sendKeys("Pechino");
        driver.findElement(By.id("dataInizio")).sendKeys("2026-02-17");
        driver.findElement(By.id("dataFine")).sendKeys("2026-04-01");
        driver.findElement(By.id("competenze")).sendKeys("Esperienza con i bambini e nell’insegnamento dell'inglese");
        driver.findElement(By.cssSelector(".btn-primary")).click();
        assertEquals(driver.findElement(By.cssSelector("strong")).getText(), "Creazione missione effettuata con successo!");
    }

    @Test
    public void TC8_2SalvataggioMissioneAccessoNegato() throws IOException {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("organizer1@earthlocals.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_ORGANIZER")));
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1534, 766));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("organizer1@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.linkText("Profilo")).click();
        driver.findElement(By.linkText("Nuova missione")).click();

        driver.manage().deleteAllCookies();

        driver.findElement(By.id("passaportoEditPassportForm")).sendKeys(getWebpFilePath());
        driver.findElement(By.id("title")).sendKeys("Help Teaching a Pechino");
        driver.findElement(By.id("description")).sendKeys("Vieni ad insegnare l’inglese in un doposcuola nel quartiere Dongsi a ragazzi delle scuole medie");
        {
            WebElement dropdown = driver.findElement(By.id("paese"));
            dropdown.findElement(By.xpath("//option[. = 'Cina']")).click();
        }
        driver.findElement(By.id("citta")).sendKeys("Pechino");
        driver.findElement(By.id("dataInizio")).sendKeys("2026-02-17");
        driver.findElement(By.id("dataFine")).sendKeys("2026-04-01");
        driver.findElement(By.id("competenze")).sendKeys("Esperienza con i bambini e nell’insegnamento dell'inglese");
        driver.findElement(By.cssSelector(".btn-primary")).click();
        assertEquals(driver.findElement(By.cssSelector("h2")).getText(), "403 - Accesso negato!");
    }

    @Test
    public void TC8_3SalvataggioMissioneNomeObbligatorio() throws IOException {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("organizer1@earthlocals.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_ORGANIZER")));
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1534, 766));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("organizer1@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.linkText("Profilo")).click();
        driver.findElement(By.linkText("Nuova missione")).click();
        driver.findElement(By.id("passaportoEditPassportForm")).sendKeys(getWebpFilePath());
        driver.findElement(By.id("title")).sendKeys(" ".repeat(5));
        driver.findElement(By.id("description")).sendKeys("Vieni ad insegnare l’inglese in un doposcuola nel quartiere Dongsi a ragazzi delle scuole medie");
        {
            WebElement dropdown = driver.findElement(By.id("paese"));
            dropdown.findElement(By.xpath("//option[. = 'Cina']")).click();
        }
        driver.findElement(By.id("citta")).sendKeys("Pechino");
        driver.findElement(By.id("dataInizio")).sendKeys("2026-02-17");
        driver.findElement(By.id("dataFine")).sendKeys("2026-04-01");
        driver.findElement(By.id("competenze")).sendKeys("Esperienza con i bambini e nell’insegnamento dell'inglese");
        driver.findElement(By.cssSelector(".btn-primary")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "Il nome della missione è obbligatorio");
    }

    @Test
    public void TC8_4SalvataggioMissioneNomeTroppoCorto() throws IOException {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("organizer1@earthlocals.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_ORGANIZER")));
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1534, 766));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("organizer1@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.linkText("Profilo")).click();
        driver.findElement(By.linkText("Nuova missione")).click();
        driver.findElement(By.id("passaportoEditPassportForm")).sendKeys(getWebpFilePath());
        driver.findElement(By.id("title")).sendKeys("a".repeat(4));
        driver.findElement(By.id("description")).sendKeys("Vieni ad insegnare l’inglese in un doposcuola nel quartiere Dongsi a ragazzi delle scuole medie");
        {
            WebElement dropdown = driver.findElement(By.id("paese"));
            dropdown.findElement(By.xpath("//option[. = 'Cina']")).click();
        }
        driver.findElement(By.id("citta")).sendKeys("Pechino");
        driver.findElement(By.id("dataInizio")).sendKeys("2026-02-17");
        driver.findElement(By.id("dataFine")).sendKeys("2026-04-01");
        driver.findElement(By.id("competenze")).sendKeys("Esperienza con i bambini e nell’insegnamento dell'inglese");
        driver.findElement(By.cssSelector(".btn-primary")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback:nth-child(3)")).getText(), "Il nome deve essere tra 5 e 100 caratteri");
    }

    @Test
    public void TC8_5SalvataggioMissioneNomeTroppoLungo() throws IOException {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("organizer1@earthlocals.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_ORGANIZER")));
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1534, 766));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("organizer1@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.linkText("Profilo")).click();
        driver.findElement(By.linkText("Nuova missione")).click();
        driver.findElement(By.id("passaportoEditPassportForm")).sendKeys(getWebpFilePath());
        driver.findElement(By.id("title")).sendKeys("a".repeat(101));
        driver.findElement(By.id("description")).sendKeys("Vieni ad insegnare l’inglese in un doposcuola nel quartiere Dongsi a ragazzi delle scuole medie");
        {
            WebElement dropdown = driver.findElement(By.id("paese"));
            dropdown.findElement(By.xpath("//option[. = 'Cina']")).click();
        }
        driver.findElement(By.id("citta")).sendKeys("Pechino");
        driver.findElement(By.id("dataInizio")).sendKeys("2026-02-17");
        driver.findElement(By.id("dataFine")).sendKeys("2026-04-01");
        driver.findElement(By.id("competenze")).sendKeys("Esperienza con i bambini e nell’insegnamento dell'inglese");
        driver.findElement(By.cssSelector(".btn-primary")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback:nth-child(3)")).getText(), "Il nome deve essere tra 5 e 100 caratteri");
    }

    @Test
    public void TC8_6SalvataggioMissioneCittaObbligatoria() throws IOException {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("organizer1@earthlocals.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_ORGANIZER")));
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1534, 766));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("organizer1@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.linkText("Profilo")).click();
        driver.findElement(By.linkText("Nuova missione")).click();
        driver.findElement(By.id("passaportoEditPassportForm")).sendKeys(getWebpFilePath());
        driver.findElement(By.id("title")).sendKeys("Help Teaching a Pechino");
        driver.findElement(By.id("description")).sendKeys("Vieni ad insegnare l’inglese in un doposcuola nel quartiere Dongsi a ragazzi delle scuole medie");
        {
            WebElement dropdown = driver.findElement(By.id("paese"));
            dropdown.findElement(By.xpath("//option[. = 'Cina']")).click();
        }
        driver.findElement(By.id("citta")).sendKeys(" ");
        driver.findElement(By.id("dataInizio")).sendKeys("2026-02-17");
        driver.findElement(By.id("dataFine")).sendKeys("2026-04-01");
        driver.findElement(By.id("competenze")).sendKeys("Esperienza con i bambini e nell’insegnamento dell'inglese");
        driver.findElement(By.cssSelector(".btn-primary")).click();
        assertEquals(driver.findElement(By.cssSelector(".row > div:nth-child(2) > .invalid-feedback")).getText(), "La città è obbligatoria");
    }

    @Test
    public void TC8_7SalvataggioMissioneDescrizioneObbligatoria() throws IOException {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("organizer1@earthlocals.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_ORGANIZER")));
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1534, 766));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("organizer1@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.linkText("Profilo")).click();
        driver.findElement(By.linkText("Nuova missione")).click();
        driver.findElement(By.id("passaportoEditPassportForm")).sendKeys(getWebpFilePath());
        driver.findElement(By.id("title")).sendKeys("Help Teaching a Pechino");
        driver.findElement(By.id("description")).sendKeys(" ");
        {
            WebElement dropdown = driver.findElement(By.id("paese"));
            dropdown.findElement(By.xpath("//option[. = 'Cina']")).click();
        }
        driver.findElement(By.id("citta")).sendKeys("Pechino");
        driver.findElement(By.id("dataInizio")).sendKeys("2026-02-17");
        driver.findElement(By.id("dataFine")).sendKeys("2026-04-01");
        driver.findElement(By.id("competenze")).sendKeys("Esperienza con i bambini e nell’insegnamento dell'inglese");
        driver.findElement(By.cssSelector(".btn-primary")).click();
        assertEquals(driver.findElement(By.cssSelector(".mb-4:nth-child(4) > .invalid-feedback:nth-child(3)")).getText(), "La descrizione è obbligatoria");
    }

    @Test
    public void TC8_8SalvataggioMissioneDescrizioneTroppoCorta() throws IOException {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("organizer1@earthlocals.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_ORGANIZER")));
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1534, 766));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("organizer1@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.linkText("Profilo")).click();
        driver.findElement(By.linkText("Nuova missione")).click();
        driver.findElement(By.id("passaportoEditPassportForm")).sendKeys(getWebpFilePath());
        driver.findElement(By.id("title")).sendKeys("Help Teaching a Pechino");
        driver.findElement(By.id("description")).sendKeys("a");
        {
            WebElement dropdown = driver.findElement(By.id("paese"));
            dropdown.findElement(By.xpath("//option[. = 'Cina']")).click();
        }
        driver.findElement(By.id("citta")).sendKeys("Pechino");
        driver.findElement(By.id("dataInizio")).sendKeys("2026-02-17");
        driver.findElement(By.id("dataFine")).sendKeys("2026-04-01");
        driver.findElement(By.id("competenze")).sendKeys("Esperienza con i bambini e nell’insegnamento dell'inglese");
        driver.findElement(By.cssSelector(".btn-primary")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "La descrizione deve essere di almeno 20 caratteri");
    }

    @Test
    public void TC8_9SalvataggioMissioneDataInizioErrata() throws IOException {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("organizer1@earthlocals.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_ORGANIZER")));
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1534, 766));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("organizer1@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.linkText("Profilo")).click();
        driver.findElement(By.linkText("Nuova missione")).click();
        driver.findElement(By.id("passaportoEditPassportForm")).sendKeys(getWebpFilePath());
        driver.findElement(By.id("title")).sendKeys("Help Teaching a Pechino");
        driver.findElement(By.id("description")).sendKeys("Vieni ad insegnare l’inglese in un doposcuola nel quartiere Dongsi a ragazzi delle scuole medie");
        {
            WebElement dropdown = driver.findElement(By.id("paese"));
            dropdown.findElement(By.xpath("//option[. = 'Cina']")).click();
        }
        driver.findElement(By.id("citta")).sendKeys("Pechino");
        driver.findElement(By.id("dataInizio")).sendKeys("2010-04-17");
        driver.findElement(By.id("dataFine")).sendKeys("2026-04-01");
        driver.findElement(By.id("competenze")).sendKeys("Esperienza con i bambini e nell’insegnamento dell'inglese");
        driver.findElement(By.cssSelector(".btn-primary")).click();
        assertEquals(driver.findElement(By.cssSelector("div:nth-child(3) > .invalid-feedback")).getText(), "La missione non può iniziare nel passato");
    }

    @Test
    public void TC8_10SalvataggioMissioneDataFineErrata() throws IOException {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("organizer1@earthlocals.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_ORGANIZER")));
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1534, 766));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("organizer1@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.linkText("Profilo")).click();
        driver.findElement(By.linkText("Nuova missione")).click();
        driver.findElement(By.id("passaportoEditPassportForm")).sendKeys(getWebpFilePath());
        driver.findElement(By.id("title")).sendKeys("Help Teaching a Pechino");
        driver.findElement(By.id("description")).sendKeys("Vieni ad insegnare l’inglese in un doposcuola nel quartiere Dongsi a ragazzi delle scuole medie");
        {
            WebElement dropdown = driver.findElement(By.id("paese"));
            dropdown.findElement(By.xpath("//option[. = 'Cina']")).click();
        }
        driver.findElement(By.id("citta")).sendKeys("Pechino");
        driver.findElement(By.id("dataInizio")).sendKeys("2010-01-17");
        driver.findElement(By.id("dataFine")).sendKeys("2010-02-17");
        driver.findElement(By.id("competenze")).sendKeys("Esperienza con i bambini e nell’insegnamento dell'inglese");
        driver.findElement(By.cssSelector(".btn-primary")).click();
        assertEquals(driver.findElement(By.cssSelector("div:nth-child(4) > .invalid-feedback")).getText(), "La data di fine deve essere nel futuro");
    }

    @Test
    public void TC8_11SalvataggioMissioneDataFineErrata() throws IOException {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("organizer1@earthlocals.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_ORGANIZER")));
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1534, 766));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("organizer1@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.linkText("Profilo")).click();
        driver.findElement(By.linkText("Nuova missione")).click();
        driver.findElement(By.id("passaportoEditPassportForm")).sendKeys(getWebpFilePath());
        driver.findElement(By.id("title")).sendKeys("Help Teaching a Pechino");
        driver.findElement(By.id("description")).sendKeys("Vieni ad insegnare l’inglese in un doposcuola nel quartiere Dongsi a ragazzi delle scuole medie");
        {
            WebElement dropdown = driver.findElement(By.id("paese"));
            dropdown.findElement(By.xpath("//option[. = 'Cina']")).click();
        }
        driver.findElement(By.id("citta")).sendKeys("Pechino");
        driver.findElement(By.id("dataInizio")).sendKeys("2026-01-14");
        driver.findElement(By.id("dataFine")).sendKeys("2026-01-15");
        driver.findElement(By.id("competenze")).sendKeys("Esperienza con i bambini e nell’insegnamento dell'inglese");
        driver.findElement(By.cssSelector(".btn-primary")).click();
        assertEquals(driver.findElement(By.cssSelector("div:nth-child(4) > .invalid-feedback")).getText(), "La data di fine deve essere nel futuro");
    }

    @Test
    public void TC8_12SalvataggioMissioneDataOverLap() throws IOException {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("organizer1@earthlocals.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_ORGANIZER")));
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1534, 766));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("organizer1@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.linkText("Profilo")).click();
        driver.findElement(By.linkText("Nuova missione")).click();
        driver.findElement(By.id("passaportoEditPassportForm")).sendKeys(getWebpFilePath());
        driver.findElement(By.id("title")).sendKeys("Help Teaching a Pechino");
        driver.findElement(By.id("description")).sendKeys("Vieni ad insegnare l’inglese in un doposcuola nel quartiere Dongsi a ragazzi delle scuole medie");
        {
            WebElement dropdown = driver.findElement(By.id("paese"));
            dropdown.findElement(By.xpath("//option[. = 'Cina']")).click();
        }
        driver.findElement(By.id("citta")).sendKeys("Pechino");
        driver.findElement(By.id("dataInizio")).sendKeys("2026-02-17");
        driver.findElement(By.id("dataFine")).sendKeys("2026-02-08");
        driver.findElement(By.id("competenze")).sendKeys("Esperienza con i bambini e nell’insegnamento dell'inglese");
        driver.findElement(By.cssSelector(".btn-primary")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "La data di fine deve essere più in avanti rispetto alla data di inizio");
    }

    @Test
    public void TC8_13SalvataggioMissioneTipoFileErrato() throws IOException {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("organizer1@earthlocals.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_ORGANIZER")));
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1534, 766));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("organizer1@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.linkText("Profilo")).click();
        driver.findElement(By.linkText("Nuova missione")).click();
        driver.findElement(By.id("passaportoEditPassportForm")).sendKeys(getFilePath());
        driver.findElement(By.id("title")).sendKeys("Help Teaching a Pechino");
        driver.findElement(By.id("description")).sendKeys("Vieni ad insegnare l’inglese in un doposcuola nel quartiere Dongsi a ragazzi delle scuole medie");
        {
            WebElement dropdown = driver.findElement(By.id("paese"));
            dropdown.findElement(By.xpath("//option[. = 'Cina']")).click();
        }
        driver.findElement(By.id("citta")).sendKeys("Pechino");
        driver.findElement(By.id("dataInizio")).sendKeys("2026-02-17");
        driver.findElement(By.id("dataFine")).sendKeys("2026-04-01");
        driver.findElement(By.id("competenze")).sendKeys("Esperienza con i bambini e nell’insegnamento dell'inglese");
        driver.findElement(By.cssSelector(".btn-primary")).click();
        assertEquals(driver.findElement(By.cssSelector(".mb-4:nth-child(2) > .invalid-feedback")).getText(), "Tipo di file non valido");
    }

    @Test
    public void TC8_14SalvataggioMissioneFallimentoConCompetenzeRichiesteVuoto() throws IOException {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("organizer1@earthlocals.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_ORGANIZER")));
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1534, 766));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("organizer1@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.linkText("Profilo")).click();
        driver.findElement(By.linkText("Nuova missione")).click();
        driver.findElement(By.id("passaportoEditPassportForm")).sendKeys(getWebpFilePath());
        driver.findElement(By.id("title")).sendKeys("Help Teaching a Pechino");
        driver.findElement(By.id("description")).sendKeys("Vieni ad insegnare l’inglese in un doposcuola nel quartiere Dongsi a ragazzi delle scuole medie");
        {
            WebElement dropdown = driver.findElement(By.id("paese"));
            dropdown.findElement(By.xpath("//option[. = 'Cina']")).click();
        }
        driver.findElement(By.id("citta")).sendKeys("Pechino");
        driver.findElement(By.id("dataInizio")).sendKeys("2026-02-17");
        driver.findElement(By.id("dataFine")).sendKeys("2026-04-01");
        driver.findElement(By.id("competenze")).sendKeys(" ");
        driver.findElement(By.cssSelector(".btn-primary")).click();
        assertEquals(driver.findElement(By.cssSelector(".mb-4:nth-child(6) > .invalid-feedback")).getText(), "Le competenze richieste sono obbligatorie");
    }


}
