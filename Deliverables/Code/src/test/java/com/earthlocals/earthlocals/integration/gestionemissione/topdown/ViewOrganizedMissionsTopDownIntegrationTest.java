package com.earthlocals.earthlocals.integration.gestionemissione.topdown;

import com.earthlocals.earthlocals.config.SystemTestAppConfig;
import com.earthlocals.earthlocals.config.TestcontainerConfig;
import com.earthlocals.earthlocals.model.*;
import com.earthlocals.earthlocals.service.gestioneemail.GestioneEmail;
import com.earthlocals.earthlocals.service.gestioneutente.passport.PassportStorageService;
import org.assertj.core.api.WithAssertions;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SystemTestAppConfig.class, TestcontainerConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ViewOrganizedMissionsTopDownIntegrationTest implements WithAssertions {

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
    public void TC10_1MissioniMoreThanOne() {

        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("organizer3@earthlocals.com");
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

        var missione1 = mock(Missione.class, RETURNS_SMART_NULLS);
        when(missione1.getStato()).thenReturn(Missione.MissioneStato.ACCETTATA);
        when(missione1.getPaese()).thenReturn(paese);
        when(missione1.getDescrizione()).thenReturn("Descrizione della missione 1");
        when(missione1.getNome()).thenReturn("Missione 1");
        when(missione1.getDataInizio()).thenReturn(LocalDate.of(2023, Month.JANUARY, 1));
        when(missione1.getDataFine()).thenReturn(LocalDate.of(2023, Month.FEBRUARY, 1));
        var missione2 = mock(Missione.class, RETURNS_SMART_NULLS);
        when(missione2.getStato()).thenReturn(Missione.MissioneStato.PENDING);
        when(missione2.getPaese()).thenReturn(paese);
        when(missione2.getDescrizione()).thenReturn("Descrizione della missione 2");
        when(missione2.getNome()).thenReturn("Missione 2");
        when(missione2.getDataInizio()).thenReturn(LocalDate.of(2023, Month.JANUARY, 1));
        when(missione2.getDataFine()).thenReturn(LocalDate.of(2023, Month.FEBRUARY, 1));
        var page = new PageImpl<>(List.of(missione1, missione2), Pageable.unpaged(), 2L);

        when(missioneRepository.findByCreatore(eq(utente), any(Pageable.class))).thenReturn(page);


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1280, 672));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("organizer3@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.linkText("Profilo")).click();
        driver.findElement(By.linkText("Gestione missioni")).click();
        List<WebElement> elements = driver.findElements(By.cssSelector(".card"));
        assertThat(!elements.isEmpty());
    }

    @Test
    public void TC10_2MissioniNotLoggedAnymore() {

        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("organizer3@earthlocals.com");
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

        var missione1 = mock(Missione.class, RETURNS_SMART_NULLS);
        when(missione1.getStato()).thenReturn(Missione.MissioneStato.ACCETTATA);
        when(missione1.getPaese()).thenReturn(paese);
        when(missione1.getDescrizione()).thenReturn("Descrizione della missione 1");
        when(missione1.getNome()).thenReturn("Missione 1");
        when(missione1.getDataInizio()).thenReturn(LocalDate.of(2023, Month.JANUARY, 1));
        when(missione1.getDataFine()).thenReturn(LocalDate.of(2023, Month.FEBRUARY, 1));
        var missione2 = mock(Missione.class, RETURNS_SMART_NULLS);
        when(missione2.getStato()).thenReturn(Missione.MissioneStato.PENDING);
        when(missione2.getPaese()).thenReturn(paese);
        when(missione2.getDescrizione()).thenReturn("Descrizione della missione 2");
        when(missione2.getNome()).thenReturn("Missione 2");
        when(missione2.getDataInizio()).thenReturn(LocalDate.of(2023, Month.JANUARY, 1));
        when(missione2.getDataFine()).thenReturn(LocalDate.of(2023, Month.FEBRUARY, 1));
        var page = new PageImpl<>(List.of(missione1, missione2), Pageable.unpaged(), 2L);

        when(missioneRepository.findByCreatore(eq(utente), any(Pageable.class))).thenReturn(page);


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1280, 672));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("organizer3@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.linkText("Profilo")).click();

        driver.manage().deleteAllCookies();

        driver.findElement(By.linkText("Gestione missioni")).click();
        assertEquals(driver.findElement(By.cssSelector(".text-center")).getText(), "Log in");
    }

    @Test
    public void TC10_3MissioniNotAuthorized() {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("volunteer@earthlocals.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode("PasswordMoltoSicura1234!"));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_VOLUNTEER")));
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.loadUserByUsername(utente.getEmail())).thenReturn(utente);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1280, 672));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("volunteer@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();
        driver.get(LocalTestWebServer.obtain(this.context).uri("/account/organizer/missions"));
        String pageSource = driver.getPageSource();

        assertThat(
            pageSource.contains("403") ||
                pageSource.contains("Accesso negato") ||
                driver.getCurrentUrl().contains("login")
        );

    }

    @Test
    public void TC10_4MissioniOrganizzateOne() {

        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("organizer3@earthlocals.com");
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

        var missione1 = mock(Missione.class, RETURNS_SMART_NULLS);
        when(missione1.getStato()).thenReturn(Missione.MissioneStato.PENDING);
        when(missione1.getPaese()).thenReturn(paese);
        when(missione1.getDescrizione()).thenReturn("Descrizione della missione 1");
        when(missione1.getNome()).thenReturn("Missione 1");
        when(missione1.getDataInizio()).thenReturn(LocalDate.of(2023, Month.JANUARY, 1));
        when(missione1.getDataFine()).thenReturn(LocalDate.of(2023, Month.FEBRUARY, 1));

        var page = new PageImpl<>(List.of(missione1), Pageable.unpaged(), 1L);
        when(missioneRepository.findByCreatore(eq(utente), any(Pageable.class))).thenReturn(page);


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1280, 672));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("organizer3@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.linkText("Profilo")).click();
        driver.findElement(By.linkText("Gestione missioni")).click();
        List<WebElement> elements = driver.findElements(By.cssSelector(".card"));
        assertEquals(1, elements.size());
    }

    @Test
    public void TC10_4MissioniNonPresenti() {

        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);

        when(utente.getEmail()).thenReturn("organizer3@earthlocals.com");
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

        var missione1 = mock(Missione.class, RETURNS_SMART_NULLS);
        when(missione1.getStato()).thenReturn(Missione.MissioneStato.ACCETTATA);
        when(missione1.getPaese()).thenReturn(paese);
        when(missione1.getDescrizione()).thenReturn("Descrizione della missione 1");
        when(missione1.getNome()).thenReturn("Missione 1");
        when(missione1.getDataInizio()).thenReturn(LocalDate.of(2023, Month.JANUARY, 1));
        when(missione1.getDataFine()).thenReturn(LocalDate.of(2023, Month.FEBRUARY, 1));
        var page = new PageImpl<Missione>(List.of(), Pageable.unpaged(), 0L);

        when(missioneRepository.findByCreatore(eq(utente), any(Pageable.class))).thenReturn(page);


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1280, 672));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("organizer3@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.linkText("Profilo")).click();
        driver.findElement(By.linkText("Gestione missioni")).click();
        List<WebElement> elements = driver.findElements(By.cssSelector(".card"));
        assertEquals(0, elements.size());
    }

}
