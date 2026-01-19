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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SystemTestAppConfig.class, TestcontainerConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilteredMissionsSearchTopDownIntegrationTest implements WithAssertions {

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
    public void TC11_1FiltroMissioniUnaPresente() {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Taiwan");
        var paese2 = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese2.getId()).thenReturn(2);
        when(paese2.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese, paese2));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));
        when(paeseRepository.findById(2)).thenReturn(Optional.of(paese2));

        var missione1 = mock(Missione.class, RETURNS_SMART_NULLS);
        when(missione1.getStato()).thenReturn(Missione.MissioneStato.ACCETTATA);
        when(missione1.getPaese()).thenReturn(paese);
        when(missione1.getDescrizione()).thenReturn("Descrizione della missione 1");
        when(missione1.getNome()).thenReturn("Missione 1");
        when(missione1.getDataInizio()).thenReturn(LocalDate.of(2023, Month.JANUARY, 1));
        when(missione1.getDataFine()).thenReturn(LocalDate.of(2023, Month.FEBRUARY, 1));

        var missione2 = mock(Missione.class, RETURNS_SMART_NULLS);
        when(missione2.getStato()).thenReturn(Missione.MissioneStato.ACCETTATA);
        when(missione2.getPaese()).thenReturn(paese2);
        when(missione2.getDescrizione()).thenReturn("Descrizione della missione 2");
        when(missione2.getNome()).thenReturn("Missione 2");
        when(missione2.getDataInizio()).thenReturn(LocalDate.of(2023, Month.JANUARY, 1));
        when(missione2.getDataFine()).thenReturn(LocalDate.of(2023, Month.FEBRUARY, 1));

        var page1 = new PageImpl<>(List.of(missione1, missione2), Pageable.unpaged(), 2L);
        var page2 = new PageImpl<>(List.of(missione1), Pageable.unpaged(), 1L);
        when(missioneRepository.findByInternalStatoAndDataFineAfter(any(), any(), any())).thenReturn(page1);
        when(missioneRepository.findByPaeseAndInternalStatoAndDataFineAfter(any(), any(), any(), any())).thenReturn(page2);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1280, 672));
        driver.findElement(By.linkText("Opportunità")).click();
        WebElement dropdown = driver.findElement(By.name("paeseId"));
        dropdown.findElement(By.xpath("//option[. = 'Taiwan']")).click();
        driver.findElement(By.cssSelector(".btn-success")).click();
        List<WebElement> elements = driver.findElements(By.cssSelector(".card-img"));
        assertEquals(1, elements.size());
    }

    @Test
    public void TC11_2FiltroMissioniDuePresenti() throws InterruptedException {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Taiwan");
        var paese2 = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese2.getId()).thenReturn(2);
        when(paese2.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese, paese2));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));
        when(paeseRepository.findById(2)).thenReturn(Optional.of(paese2));

        var missione1 = mock(Missione.class, RETURNS_SMART_NULLS);
        when(missione1.getStato()).thenReturn(Missione.MissioneStato.ACCETTATA);
        when(missione1.getPaese()).thenReturn(paese);
        when(missione1.getDescrizione()).thenReturn("Descrizione della missione 1");
        when(missione1.getNome()).thenReturn("Missione 1");
        when(missione1.getDataInizio()).thenReturn(LocalDate.of(2023, Month.JANUARY, 1));
        when(missione1.getDataFine()).thenReturn(LocalDate.of(2023, Month.FEBRUARY, 1));

        var missione2 = mock(Missione.class, RETURNS_SMART_NULLS);
        when(missione2.getStato()).thenReturn(Missione.MissioneStato.ACCETTATA);
        when(missione2.getPaese()).thenReturn(paese2);
        when(missione2.getDescrizione()).thenReturn("Descrizione della missione 2");
        when(missione2.getNome()).thenReturn("Missione 2");
        when(missione2.getDataInizio()).thenReturn(LocalDate.of(2023, Month.JANUARY, 1));
        when(missione2.getDataFine()).thenReturn(LocalDate.of(2023, Month.FEBRUARY, 1));

        var missione3 = mock(Missione.class, RETURNS_SMART_NULLS);
        when(missione3.getStato()).thenReturn(Missione.MissioneStato.ACCETTATA);
        when(missione3.getPaese()).thenReturn(paese);
        when(missione3.getDescrizione()).thenReturn("Descrizione della missione 3");
        when(missione3.getNome()).thenReturn("Missione 3");
        when(missione3.getDataInizio()).thenReturn(LocalDate.of(2023, Month.JANUARY, 1));
        when(missione3.getDataFine()).thenReturn(LocalDate.of(2023, Month.FEBRUARY, 1));

        var page1 = new PageImpl<>(List.of(missione1, missione2, missione3), Pageable.unpaged(), 3L);
        var page2 = new PageImpl<>(List.of(missione1, missione3), Pageable.unpaged(), 2L);
        when(missioneRepository.findByInternalStatoAndDataFineAfter(any(), any(), any())).thenReturn(page1);
        when(missioneRepository.findByPaeseAndInternalStatoAndDataFineAfter(any(), any(), any(), any())).thenReturn(page2);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1280, 672));
        driver.findElement(By.linkText("Opportunità")).click();
        WebElement dropdown = driver.findElement(By.name("paeseId"));
        dropdown.findElement(By.xpath("//option[. = 'Taiwan']")).click();
        driver.findElement(By.cssSelector(".btn-success")).click();
        List<WebElement> elements = driver.findElements(By.cssSelector(".card-img"));
        assertEquals(2, elements.size());
    }

    @Test
    public void TC11_3FiltroMissioniNessuna() {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Taiwan");
        var paese2 = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese2.getId()).thenReturn(2);
        when(paese2.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese, paese2));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));
        when(paeseRepository.findById(2)).thenReturn(Optional.of(paese2));

        var missione1 = mock(Missione.class, RETURNS_SMART_NULLS);
        when(missione1.getStato()).thenReturn(Missione.MissioneStato.ACCETTATA);
        when(missione1.getPaese()).thenReturn(paese2);
        when(missione1.getDescrizione()).thenReturn("Descrizione della missione 1");
        when(missione1.getNome()).thenReturn("Missione 1");
        when(missione1.getDataInizio()).thenReturn(LocalDate.of(2023, Month.JANUARY, 1));
        when(missione1.getDataFine()).thenReturn(LocalDate.of(2023, Month.FEBRUARY, 1));

        var missione2 = mock(Missione.class, RETURNS_SMART_NULLS);
        when(missione2.getStato()).thenReturn(Missione.MissioneStato.ACCETTATA);
        when(missione2.getPaese()).thenReturn(paese2);
        when(missione2.getDescrizione()).thenReturn("Descrizione della missione 2");
        when(missione2.getNome()).thenReturn("Missione 2");
        when(missione2.getDataInizio()).thenReturn(LocalDate.of(2023, Month.JANUARY, 1));
        when(missione2.getDataFine()).thenReturn(LocalDate.of(2023, Month.FEBRUARY, 1));

        var page1 = new PageImpl<>(List.of(missione1, missione2), Pageable.unpaged(), 2L);
        var page2 = new PageImpl<Missione>(List.of(), Pageable.unpaged(), 0L);
        when(missioneRepository.findByInternalStatoAndDataFineAfter(any(), any(), any())).thenReturn(page1);
        when(missioneRepository.findByPaeseAndInternalStatoAndDataFineAfter(any(), any(), any(), any())).thenReturn(page2);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1280, 672));
        driver.findElement(By.linkText("Opportunità")).click();
        WebElement dropdown = driver.findElement(By.name("paeseId"));
        dropdown.findElement(By.xpath("//option[. = 'Taiwan']")).click();
        driver.findElement(By.cssSelector(".btn-success")).click();
        List<WebElement> elements = driver.findElements(By.cssSelector(".card-img"));
        assertEquals(0, elements.size());
    }

    @Test
    public void TC11_4FiltroNotSelected() {
        var paese = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese.getId()).thenReturn(1);
        when(paese.getNome()).thenReturn("Taiwan");
        var paese2 = mock(Paese.class, RETURNS_SMART_NULLS);
        when(paese2.getId()).thenReturn(2);
        when(paese2.getNome()).thenReturn("Cina");
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese, paese2));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese));
        when(paeseRepository.findById(2)).thenReturn(Optional.of(paese2));

        var missione1 = mock(Missione.class, RETURNS_SMART_NULLS);
        when(missione1.getStato()).thenReturn(Missione.MissioneStato.ACCETTATA);
        when(missione1.getPaese()).thenReturn(paese2);
        when(missione1.getDescrizione()).thenReturn("Descrizione della missione 1");
        when(missione1.getNome()).thenReturn("Missione 1");
        when(missione1.getDataInizio()).thenReturn(LocalDate.of(2023, Month.JANUARY, 1));
        when(missione1.getDataFine()).thenReturn(LocalDate.of(2023, Month.FEBRUARY, 1));

        var missione2 = mock(Missione.class, RETURNS_SMART_NULLS);
        when(missione2.getStato()).thenReturn(Missione.MissioneStato.ACCETTATA);
        when(missione2.getPaese()).thenReturn(paese2);
        when(missione2.getDescrizione()).thenReturn("Descrizione della missione 2");
        when(missione2.getNome()).thenReturn("Missione 2");
        when(missione2.getDataInizio()).thenReturn(LocalDate.of(2023, Month.JANUARY, 1));
        when(missione2.getDataFine()).thenReturn(LocalDate.of(2023, Month.FEBRUARY, 1));

        var page1 = new PageImpl<>(List.of(missione1, missione2), Pageable.unpaged(), 2L);
        var page2 = new PageImpl<Missione>(List.of(), Pageable.unpaged(), 0L);
        when(missioneRepository.findByInternalStatoAndDataFineAfter(any(), any(), any())).thenReturn(page1);
        when(missioneRepository.findByPaeseAndInternalStatoAndDataFineAfter(any(), any(), any(), any())).thenReturn(page2);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1280, 672));
        driver.findElement(By.linkText("Opportunità")).click();
        List<WebElement> elements = driver.findElements(By.cssSelector(".card-img"));
        assertEquals(2, elements.size());
    }


}
