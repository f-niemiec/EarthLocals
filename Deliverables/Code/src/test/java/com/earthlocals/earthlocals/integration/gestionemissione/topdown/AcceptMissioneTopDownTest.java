package com.earthlocals.earthlocals.integration.gestionemissione.topdown;

import com.earthlocals.earthlocals.config.SystemTestAppConfig;
import com.earthlocals.earthlocals.config.TestcontainerConfig;
import com.earthlocals.earthlocals.model.*;
import com.earthlocals.earthlocals.service.gestionemissioni.GestioneMissione;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.WebApplicationContext;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SystemTestAppConfig.class, TestcontainerConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AcceptMissioneTopDownTest {

    JavascriptExecutor js;
    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private PaeseRepository paeseRepository;
    @MockitoBean(answers = Answers.RETURNS_SMART_NULLS)
    private MissioneRepository missioneRepository;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private GestioneMissione gestioneMissione;
    private WebDriver driver;
    private Map<String, Object> vars;

    @BeforeEach
    public void setUp(WebApplicationContext context) {
        FirefoxOptions options = new FirefoxOptions();
        //options.addArguments("-headless");
        driver = new FirefoxDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));

        js = (JavascriptExecutor) driver;
        vars = new HashMap<String, Object>();
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void acceptMissione() {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);
        when(utente.getEmail()).thenReturn("moderator@earthlocals.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode("test"));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_MODERATOR")));
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        var id = 1L;
        var missione1 = mock(Missione.class, RETURNS_SMART_NULLS);
        when(missione1.getId()).thenReturn(id);
        when(missione1.getNome()).thenReturn("Titolo 1");
        when(missione1.getStato()).thenReturn(Missione.MissioneStato.PENDING);
        when(missione1.getPaese()).thenReturn(paese);
        when(missione1.getDescrizione()).thenReturn("Descrizione della missione 1");
        when(missione1.getNome()).thenReturn("Missione 1");
        when(missione1.getDataInizio()).thenReturn(LocalDate.of(2023, Month.JANUARY, 1));
        when(missione1.getDataFine()).thenReturn(LocalDate.of(2023, Month.FEBRUARY, 1));
        when(missione1.accettaMissione()).thenReturn(true);

        var page = new PageImpl<>(List.of(missione1), Pageable.unpaged(), 1L);
        when(missioneRepository.findByInternalStatoAndDataFineAfter(eq(Missione.InternalMissioneStato.PENDING), any(), any(Pageable.class))).thenReturn(page);
        when(missioneRepository.findById(1L)).thenReturn(Optional.of(missione1));


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1280, 672));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("moderator@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).click();
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("test");
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.linkText("Profilo")).click();
        driver.findElement(By.linkText("Gestione missioni")).click();
        var button = driver.findElement(By.cssSelector("button[title='Approva']"));

        var dataBsTarget = button.getAttribute("data-bs-target");
        assertNotNull(dataBsTarget);
        assertNotEquals(0, driver.findElements(By.cssSelector(dataBsTarget)).size());
        button.click();


        var pageEmpty = new PageImpl<Missione>(List.of(), Pageable.unpaged(), 0L);

        when(missioneRepository.findByInternalStatoAndDataFineAfter(eq(Missione.InternalMissioneStato.PENDING), any(), any(Pageable.class))).thenReturn(pageEmpty);


        driver.findElement(By.cssSelector("div[aria-modal=true] form > .btn")).click();


        assertEquals(0, driver.findElements(By.cssSelector(dataBsTarget)).size());
    }

    @Test
    void AcceptMissioneNotLoggedAnymore() {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));

        var utente = mock(Utente.class, RETURNS_SMART_NULLS);
        when(utente.getEmail()).thenReturn("moderator@earthlocals.com");
        when(utente.getPassword()).thenReturn(passwordEncoder.encode("test"));
        when(utente.isEnabled()).thenReturn(true);
        when(utente.isAccountNonExpired()).thenReturn(true);
        when(utente.isAccountNonLocked()).thenReturn(true);
        when(utente.isCredentialsNonExpired()).thenReturn(true);
        when(utente.getAuthorities()).thenReturn((Collection) Set.of(new SimpleGrantedAuthority("ROLE_MODERATOR")));
        when(utente.getNazionalita()).thenReturn(paese);
        when(utente.getDataNascita()).thenReturn(LocalDate.of(2004, Month.APRIL, 1));
        when(utente.getSesso()).thenReturn('M');
        when(utente.getPending()).thenReturn(false);

        var id = 1L;
        var missione1 = mock(Missione.class, RETURNS_SMART_NULLS);
        when(missione1.getId()).thenReturn(id);
        when(missione1.getNome()).thenReturn("Titolo 1");
        when(missione1.getStato()).thenReturn(Missione.MissioneStato.PENDING);
        when(missione1.getPaese()).thenReturn(paese);
        when(missione1.getDescrizione()).thenReturn("Descrizione della missione 1");
        when(missione1.getNome()).thenReturn("Missione 1");
        when(missione1.getDataInizio()).thenReturn(LocalDate.of(2023, Month.JANUARY, 1));
        when(missione1.getDataFine()).thenReturn(LocalDate.of(2023, Month.FEBRUARY, 1));
        when(missione1.accettaMissione()).thenReturn(true);

        var page = new PageImpl<>(List.of(missione1), Pageable.unpaged(), 1L);
        when(missioneRepository.findByInternalStatoAndDataFineAfter(eq(Missione.InternalMissioneStato.PENDING), any(), any(Pageable.class))).thenReturn(page);
        when(missioneRepository.findById(1L)).thenReturn(Optional.of(missione1));


        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1280, 672));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.id("inputEmailLoginForm")).click();
        driver.findElement(By.id("inputEmailLoginForm")).sendKeys("moderator@earthlocals.com");
        driver.findElement(By.id("inputPasswordLoginForm")).click();
        driver.findElement(By.id("inputPasswordLoginForm")).sendKeys("test");
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.linkText("Profilo")).click();
        driver.findElement(By.linkText("Gestione missioni")).click();
        driver.manage().deleteAllCookies();
        var button = driver.findElement(By.cssSelector("button[title='Approva']"));

        var dataBsTarget = button.getAttribute("data-bs-target");
        assertNotNull(dataBsTarget);
        assertNotEquals(0, driver.findElements(By.cssSelector(dataBsTarget)).size());
        button.click();

        driver.findElement(By.cssSelector("div[aria-modal=true] form > .btn")).click();

        assertEquals(driver.findElement(By.cssSelector("h2")).getText(), "403 - Accesso negato!");
    }


}
