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
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.http.server.LocalTestWebServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.time.Duration;
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
public class VolunteerRegistrationTopDownIntegrationTest {

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
    public void TC1_1RegistrazioneVolontarioConSuccesso() throws IOException {
        var paese1 = mock(Paese.class);
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese1));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese1));
        when(paese1.getNome()).thenReturn("Italia");
        when(paese1.getId()).thenReturn(1);
        when(utenteRepository.findByEmail("andrea.squitieri@mail.com")).thenReturn(null);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1908, 1023));
        driver.findElement(By.linkText("Iscriviti")).click();
        driver.findElement(By.id("firstNameRegistrationForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameRegistrationForm")).sendKeys("Squitieri");
        driver.findElement(By.id("emailRegistrationForm")).sendKeys("andrea.squitieri@mail.com");
        driver.findElement(By.id("passwordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.id("confirmPasswordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        {
            WebElement dropdown = driver.findElement(By.id("nazionalitaRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Italia']")).click();
        }
        driver.findElement(By.id("dataNascitaRegistrationForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("numeroPassaportoRegistrationForm")).sendKeys("ASPASS");
        driver.findElement(By.id("dataScadenzaPassaportoRegistrationForm")).sendKeys("2099-04-01");
        driver.findElement(By.id("dataEmissionePassaportoRegistrationForm")).sendKeys("2010-04-01");
        driver.findElement(By.id("passaportoRegistrationForm")).sendKeys(getFilePath());
        {
            WebElement dropdown = driver.findElement(By.id("sessoRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Maschio']")).click();
        }
        driver.findElement(By.cssSelector("#sessoRegistrationForm > option:nth-child(2)")).click();
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.id("loginFormContainer")).click();
        assertEquals(
            "Ti abbiamo inviato una mail!\nConferma l'attivazione dell'account accedendo attraverso il link che trovi nella tua casella di posta elettronica.",
            driver.findElement(By.cssSelector(".alert")).getText());
    }

    @Test
    public void TC1_2RegistrazioneVolontarioConEmailPresente() throws IOException {
        var paese1 = mock(Paese.class);
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese1));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese1));
        when(paese1.getNome()).thenReturn("Italia");
        when(paese1.getId()).thenReturn(1);

        var utente = mock(Utente.class);
        when(utente.getPending()).thenReturn(false);

        when(utenteRepository.findByEmail("andrea.squitieri@mail.com")).thenReturn(utente);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1908, 1023));
        driver.findElement(By.linkText("Iscriviti")).click();
        driver.findElement(By.id("firstNameRegistrationForm")).click();
        driver.findElement(By.id("firstNameRegistrationForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameRegistrationForm")).sendKeys("Squitieri");
        driver.findElement(By.id("emailRegistrationForm")).sendKeys("andrea.squitieri@mail.com");
        driver.findElement(By.id("passwordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.id("confirmPasswordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        {
            WebElement dropdown = driver.findElement(By.id("nazionalitaRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Italia']")).click();
        }
        {
            WebElement element = driver.findElement(By.id("confirmPasswordRegistrationForm"));
            Actions builder = new Actions(driver);
            builder.doubleClick(element).perform();
        }
        driver.findElement(By.id("dataNascitaRegistrationForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("numeroPassaportoRegistrationForm")).sendKeys("ASPASS");
        driver.findElement(By.id("dataScadenzaPassaportoRegistrationForm")).click();
        driver.findElement(By.id("emailRegistrationForm")).click();
        driver.findElement(By.id("emailRegistrationForm")).click();
        {
            WebElement element = driver.findElement(By.id("emailRegistrationForm"));
            Actions builder = new Actions(driver);
            builder.doubleClick(element).perform();
        }
        driver.findElement(By.id("emailRegistrationForm")).click();
        driver.findElement(By.id("dataScadenzaPassaportoRegistrationForm")).click();
        driver.findElement(By.id("dataScadenzaPassaportoRegistrationForm")).sendKeys("2099-04-01");
        driver.findElement(By.id("dataEmissionePassaportoRegistrationForm")).sendKeys("2010-04-01");
        driver.findElement(By.id("passaportoRegistrationForm")).sendKeys(getFilePath());
        driver.findElement(By.id("sessoRegistrationForm")).click();
        {
            WebElement dropdown = driver.findElement(By.id("sessoRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Maschio']")).click();
        }
        driver.findElement(By.cssSelector("#sessoRegistrationForm > option:nth-child(2)")).click();
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.cssSelector(".invalid-feedback")).click();
        driver.findElement(By.cssSelector(".invalid-feedback")).click();
        {
            WebElement element = driver.findElement(By.cssSelector(".invalid-feedback"));
            Actions builder = new Actions(driver);
            builder.doubleClick(element).perform();
        }
        driver.findElement(By.cssSelector(".invalid-feedback")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "L'email è già registrata");
    }

    @Test
    public void TC1_3RegistrazioneVolontarioFormatoNomeErrato() throws IOException {
        var paese1 = mock(Paese.class);
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese1));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese1));
        when(paese1.getNome()).thenReturn("Italia");
        when(paese1.getId()).thenReturn(1);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1550, 830));
        driver.findElement(By.linkText("Iscriviti")).click();
        driver.findElement(By.id("firstNameRegistrationForm")).sendKeys(" ");
        driver.findElement(By.id("lastNameRegistrationForm")).sendKeys("Squitieri");
        driver.findElement(By.id("emailRegistrationForm")).sendKeys("andrea.squitieri@mail.com");
        driver.findElement(By.id("passwordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.id("confirmPasswordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        {
            WebElement dropdown = driver.findElement(By.id("nazionalitaRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Italia']")).click();
        }
        driver.findElement(By.id("dataNascitaRegistrationForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("numeroPassaportoRegistrationForm")).sendKeys("ASPASS");
        driver.findElement(By.id("dataScadenzaPassaportoRegistrationForm")).sendKeys("2099-04-01");
        driver.findElement(By.id("dataEmissionePassaportoRegistrationForm")).sendKeys("2010-04-01");
        driver.findElement(By.id("passaportoRegistrationForm")).sendKeys(getFilePath());
        {
            WebElement dropdown = driver.findElement(By.id("sessoRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Maschio']")).click();
        }
        driver.findElement(By.cssSelector("#sessoRegistrationForm > option:nth-child(2)")).click();
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "Il nome è obbligatorio");
    }

    @Test
    public void TC1_4RegistrazioneFormatoCognomeErrato() throws IOException {
        var paese1 = mock(Paese.class);
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese1));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese1));
        when(paese1.getNome()).thenReturn("Italia");
        when(paese1.getId()).thenReturn(1);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1908, 1023));
        driver.findElement(By.linkText("Iscriviti")).click();
        driver.findElement(By.id("firstNameRegistrationForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameRegistrationForm")).sendKeys(" ");
        driver.findElement(By.id("emailRegistrationForm")).sendKeys("andrea.squitieri@mail.com");
        driver.findElement(By.id("passwordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.id("confirmPasswordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        {
            WebElement dropdown = driver.findElement(By.id("nazionalitaRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Italia']")).click();
        }
        driver.findElement(By.id("dataNascitaRegistrationForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("numeroPassaportoRegistrationForm")).sendKeys("ASPASS");
        driver.findElement(By.id("dataScadenzaPassaportoRegistrationForm")).sendKeys("2099-04-01");
        driver.findElement(By.id("dataEmissionePassaportoRegistrationForm")).sendKeys("2010-04-01");
        driver.findElement(By.id("passaportoRegistrationForm")).sendKeys(getFilePath());
        {
            WebElement dropdown = driver.findElement(By.id("sessoRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Maschio']")).click();
        }
        driver.findElement(By.cssSelector("#sessoRegistrationForm > option:nth-child(2)")).click();
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "Il cognome è obbligatorio");
    }

    @Test
    public void TC1_5RegistrazioneVolontarioFormatoDataNascitaErrato() throws IOException {
        var paese1 = mock(Paese.class);
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese1));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese1));
        when(paese1.getNome()).thenReturn("Italia");
        when(paese1.getId()).thenReturn(1);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1550, 830));
        driver.findElement(By.linkText("Iscriviti")).click();
        driver.findElement(By.id("firstNameRegistrationForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameRegistrationForm")).sendKeys("Squitieri");
        driver.findElement(By.id("emailRegistrationForm")).sendKeys("andrea.squitieri@mail.com");
        driver.findElement(By.id("passwordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.id("confirmPasswordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.id("nazionalitaRegistrationForm")).click();
        {
            WebElement dropdown = driver.findElement(By.id("nazionalitaRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Italia']")).click();
        }
        driver.findElement(By.id("dataNascitaRegistrationForm")).sendKeys("2030-04-01");
        driver.findElement(By.id("numeroPassaportoRegistrationForm")).sendKeys("ASPASS");
        driver.findElement(By.id("dataScadenzaPassaportoRegistrationForm")).sendKeys("2099-04-01");
        driver.findElement(By.id("dataEmissionePassaportoRegistrationForm")).sendKeys("2010-04-01");
        driver.findElement(By.id("passaportoRegistrationForm")).sendKeys(getFilePath());
        {
            WebElement dropdown = driver.findElement(By.id("sessoRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Maschio']")).click();
        }
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.cssSelector(".invalid-feedback")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "La data di nascita inserita non è valida");
    }

    @Test
    public void TC1_6RegistrazioneVolontarioDataScadenzaPassaportoNonValida() throws IOException {
        var paese1 = mock(Paese.class);
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese1));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese1));
        when(paese1.getNome()).thenReturn("Italia");
        when(paese1.getId()).thenReturn(1);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1550, 830));
        driver.findElement(By.linkText("Iscriviti")).click();
        driver.findElement(By.id("firstNameRegistrationForm")).click();
        driver.findElement(By.id("firstNameRegistrationForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameRegistrationForm")).sendKeys("Squitieri");
        driver.findElement(By.id("emailRegistrationForm")).sendKeys("andrea.squitieri@mail.com");
        driver.findElement(By.id("passwordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.id("confirmPasswordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        {
            WebElement dropdown = driver.findElement(By.id("nazionalitaRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Italia']")).click();
        }
        driver.findElement(By.id("dataNascitaRegistrationForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("numeroPassaportoRegistrationForm")).sendKeys("ASPASS");
        driver.findElement(By.id("dataScadenzaPassaportoRegistrationForm")).sendKeys("2010-04-01");
        driver.findElement(By.id("dataEmissionePassaportoRegistrationForm")).sendKeys("2010-04-01");
        driver.findElement(By.id("passaportoRegistrationForm")).sendKeys(getFilePath());
        driver.findElement(By.id("sessoRegistrationForm")).click();
        {
            WebElement dropdown = driver.findElement(By.id("sessoRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Maschio']")).click();
        }
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.id("registrationForm")).click();
        assertEquals(driver.findElement(By.cssSelector("div:nth-child(11) > .invalid-feedback")).getText(), "La data di scadenza del passaporto inserita non è valida");
    }

    @Test
    public void TC1_7RegistrazioneVolontarioDataScadenzaSuccessivaDataEmissione() throws IOException {
        var paese1 = mock(Paese.class);
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese1));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese1));
        when(paese1.getNome()).thenReturn("Italia");
        when(paese1.getId()).thenReturn(1);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1550, 830));
        driver.findElement(By.linkText("Iscriviti")).click();
        driver.findElement(By.id("firstNameRegistrationForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameRegistrationForm")).sendKeys("Squitieri");
        driver.findElement(By.id("emailRegistrationForm")).sendKeys("andrea.squitieri@mail.com");
        driver.findElement(By.id("passwordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.id("confirmPasswordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        {
            WebElement dropdown = driver.findElement(By.id("nazionalitaRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Italia']")).click();
        }
        driver.findElement(By.id("dataNascitaRegistrationForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("numeroPassaportoRegistrationForm")).sendKeys("ASPASS");
        driver.findElement(By.id("dataScadenzaPassaportoRegistrationForm")).sendKeys("2099-03-01");
        driver.findElement(By.id("dataEmissionePassaportoRegistrationForm")).sendKeys("2099-04-01");
        driver.findElement(By.id("passaportoRegistrationForm")).sendKeys(getFilePath());
        {
            WebElement dropdown = driver.findElement(By.id("sessoRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Maschio']")).click();
        }
        driver.findElement(By.cssSelector(".btn")).click();
        driver.findElement(By.id("registrationForm")).click();
        assertEquals(driver.findElement(By.cssSelector("div:nth-child(11) > .invalid-feedback")).getText(), "La data di scadenza deve essere successiva alla data di emissione");
    }

    @Test
    public void TC1_8RegistrazioneVolontarioDataEmissionePassaportoNonValida() throws IOException {
        var paese1 = mock(Paese.class);
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese1));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese1));
        when(paese1.getNome()).thenReturn("Italia");
        when(paese1.getId()).thenReturn(1);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1550, 830));
        driver.findElement(By.linkText("Iscriviti")).click();
        driver.findElement(By.id("firstNameRegistrationForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameRegistrationForm")).sendKeys("Squitieri");
        driver.findElement(By.id("emailRegistrationForm")).sendKeys("andrea.squitieri@mail.com");
        driver.findElement(By.id("passwordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.id("confirmPasswordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        {
            WebElement dropdown = driver.findElement(By.id("nazionalitaRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Italia']")).click();
        }
        driver.findElement(By.id("dataNascitaRegistrationForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("numeroPassaportoRegistrationForm")).sendKeys("ASPASS");
        driver.findElement(By.id("dataScadenzaPassaportoRegistrationForm")).sendKeys("2099-04-01");
        driver.findElement(By.id("dataEmissionePassaportoRegistrationForm")).sendKeys("2080-04-01");
        driver.findElement(By.id("passaportoRegistrationForm")).sendKeys(getFilePath());
        {
            WebElement dropdown = driver.findElement(By.id("sessoRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Maschio']")).click();
        }
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "La data di emissione del passaporto inserita non è valida");
    }

    @Test
    public void TC1_9RegistrazioneVolontarioEmailNonValidaUsernameMancante() throws IOException {
        var paese1 = mock(Paese.class);
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese1));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese1));
        when(paese1.getNome()).thenReturn("Italia");
        when(paese1.getId()).thenReturn(1);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1550, 830));
        driver.findElement(By.linkText("Iscriviti")).click();
        driver.findElement(By.id("firstNameRegistrationForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameRegistrationForm")).sendKeys("Squitieri");
        driver.findElement(By.id("emailRegistrationForm")).sendKeys("@mail.com");
        driver.findElement(By.id("passwordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.id("confirmPasswordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        {
            WebElement dropdown = driver.findElement(By.id("nazionalitaRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Italia']")).click();
        }
        driver.findElement(By.id("dataNascitaRegistrationForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("numeroPassaportoRegistrationForm")).sendKeys("ASPASS");
        driver.findElement(By.id("dataScadenzaPassaportoRegistrationForm")).sendKeys("2099-04-01");
        driver.findElement(By.id("dataEmissionePassaportoRegistrationForm")).sendKeys("2010-04-01");
        driver.findElement(By.id("passaportoRegistrationForm")).sendKeys(getFilePath());
        {
            WebElement dropdown = driver.findElement(By.id("sessoRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Maschio']")).click();
        }
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector("div:nth-child(4) > .invalid-feedback")).getText(), "L'email inserita non è valida");
    }

    @Test
    public void TC1_10RegistrazioneVolontarioEmailNonValidaChiocciolaMancante() throws IOException {
        var paese1 = mock(Paese.class);
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese1));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese1));
        when(paese1.getNome()).thenReturn("Italia");
        when(paese1.getId()).thenReturn(1);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1550, 830));
        driver.findElement(By.linkText("Iscriviti")).click();
        driver.findElement(By.id("firstNameRegistrationForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameRegistrationForm")).sendKeys("Squitieri");
        driver.findElement(By.id("emailRegistrationForm")).sendKeys("andrea.squitiermail.com");
        driver.findElement(By.id("passwordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.id("confirmPasswordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        {
            WebElement dropdown = driver.findElement(By.id("nazionalitaRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Italia']")).click();
        }
        driver.findElement(By.id("dataNascitaRegistrationForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("numeroPassaportoRegistrationForm")).sendKeys("ASPASS");
        driver.findElement(By.id("dataScadenzaPassaportoRegistrationForm")).sendKeys("2099-04-01");
        driver.findElement(By.id("dataEmissionePassaportoRegistrationForm")).sendKeys("2010-04-01");
        driver.findElement(By.id("passaportoRegistrationForm")).sendKeys(getFilePath());
        {
            WebElement dropdown = driver.findElement(By.id("sessoRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Maschio']")).click();
        }
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector("div:nth-child(4) > .invalid-feedback")).getText(), "L'email inserita non è valida");
    }

    @Test
    public void TC1_11RegistrazioneVolontarioEmailNonValidaDominioMancante() throws IOException {
        var paese1 = mock(Paese.class);
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese1));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese1));
        when(paese1.getNome()).thenReturn("Italia");
        when(paese1.getId()).thenReturn(1);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1550, 830));
        driver.findElement(By.linkText("Iscriviti")).click();
        driver.findElement(By.id("firstNameRegistrationForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameRegistrationForm")).sendKeys("Squitieri");
        driver.findElement(By.id("emailRegistrationForm")).sendKeys("andrea.squitieri@");
        driver.findElement(By.id("passwordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.id("confirmPasswordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        {
            WebElement dropdown = driver.findElement(By.id("nazionalitaRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Italia']")).click();
        }
        driver.findElement(By.id("dataNascitaRegistrationForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("numeroPassaportoRegistrationForm")).sendKeys("ASPASS");
        driver.findElement(By.id("dataScadenzaPassaportoRegistrationForm")).sendKeys("2099-04-01");
        driver.findElement(By.id("dataEmissionePassaportoRegistrationForm")).sendKeys("2010-04-01");
        driver.findElement(By.id("passaportoRegistrationForm")).sendKeys(getFilePath());
        {
            WebElement dropdown = driver.findElement(By.id("sessoRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Maschio']")).click();
        }
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector("div:nth-child(4) > .invalid-feedback")).getText(), "L'email inserita non è valida");
    }

    @Test
    public void TC1_12RegistrazioneVolontarioPasswordCarattereSpecialeMancante() throws IOException {
        var paese1 = mock(Paese.class);
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese1));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese1));
        when(paese1.getNome()).thenReturn("Italia");
        when(paese1.getId()).thenReturn(1);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1550, 830));
        driver.findElement(By.linkText("Iscriviti")).click();
        driver.findElement(By.id("firstNameRegistrationForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameRegistrationForm")).sendKeys("Squitieri");
        driver.findElement(By.id("emailRegistrationForm")).sendKeys("andrea.squitieri@mail.com");
        driver.findElement(By.id("passwordRegistrationForm")).sendKeys("Pwnnsicur4");
        driver.findElement(By.id("confirmPasswordRegistrationForm")).sendKeys("Pwnnsicur4");
        {
            WebElement dropdown = driver.findElement(By.id("nazionalitaRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Italia']")).click();
        }
        driver.findElement(By.id("dataNascitaRegistrationForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("numeroPassaportoRegistrationForm")).sendKeys("ASPASS");
        driver.findElement(By.id("dataScadenzaPassaportoRegistrationForm")).sendKeys("2099-04-01");
        driver.findElement(By.id("dataEmissionePassaportoRegistrationForm")).sendKeys("2010-04-01");
        driver.findElement(By.id("passaportoRegistrationForm")).sendKeys(getFilePath());
        {
            WebElement dropdown = driver.findElement(By.id("sessoRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Maschio']")).click();
        }
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback:nth-child(3)")).getText(), "La password deve contenere almeno un carattere speciale");
    }

    @Test
    public void TC1_13RegistrazioneVolontarioPasswordCarattereMinuscoloMancante() throws IOException {
        var paese1 = mock(Paese.class);
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese1));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese1));
        when(paese1.getNome()).thenReturn("Italia");
        when(paese1.getId()).thenReturn(1);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1550, 830));
        driver.findElement(By.linkText("Iscriviti")).click();
        driver.findElement(By.id("firstNameRegistrationForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameRegistrationForm")).sendKeys("Squitieri");
        driver.findElement(By.id("emailRegistrationForm")).sendKeys("andrea.squitieri@mail.com");
        driver.findElement(By.id("passwordRegistrationForm")).sendKeys("PWNNS!CUR4");
        driver.findElement(By.id("confirmPasswordRegistrationForm")).sendKeys("PWNNS!CUR4");
        {
            WebElement dropdown = driver.findElement(By.id("nazionalitaRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Italia']")).click();
        }
        driver.findElement(By.id("dataNascitaRegistrationForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("numeroPassaportoRegistrationForm")).sendKeys("ASPASS");
        driver.findElement(By.id("dataScadenzaPassaportoRegistrationForm")).sendKeys("2099-04-01");
        driver.findElement(By.id("dataEmissionePassaportoRegistrationForm")).sendKeys("2010-04-01");
        driver.findElement(By.id("passaportoRegistrationForm")).sendKeys(getFilePath());
        {
            WebElement dropdown = driver.findElement(By.id("sessoRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Maschio']")).click();
        }
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback:nth-child(3)")).getText(), "La password deve contenere almeno un carattere minuscolo");
    }

    @Test
    public void TC1_14RegistrazioneVolontarioPasswordCarattereMaiuscoloMancante() throws IOException {
        var paese1 = mock(Paese.class);
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese1));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese1));
        when(paese1.getNome()).thenReturn("Italia");
        when(paese1.getId()).thenReturn(1);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1550, 830));
        driver.findElement(By.linkText("Iscriviti")).click();
        driver.findElement(By.id("firstNameRegistrationForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameRegistrationForm")).sendKeys("Squitieri");
        driver.findElement(By.id("emailRegistrationForm")).sendKeys("andrea.squitieri@mail.com");
        driver.findElement(By.id("passwordRegistrationForm")).sendKeys("pwnns!cur4");
        driver.findElement(By.id("confirmPasswordRegistrationForm")).sendKeys("pwnns!cur4");
        {
            WebElement dropdown = driver.findElement(By.id("nazionalitaRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Italia']")).click();
        }
        driver.findElement(By.id("dataNascitaRegistrationForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("numeroPassaportoRegistrationForm")).sendKeys("ASPASS");
        driver.findElement(By.id("dataScadenzaPassaportoRegistrationForm")).sendKeys("2099-04-01");
        driver.findElement(By.id("dataEmissionePassaportoRegistrationForm")).sendKeys("2010-04-01");
        driver.findElement(By.id("passaportoRegistrationForm")).sendKeys(getFilePath());
        {
            WebElement dropdown = driver.findElement(By.id("sessoRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Maschio']")).click();
        }
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback:nth-child(3)")).getText(), "La password deve contenere almeno un carattere maiuscolo");
    }

    @Test
    public void TC1_15RegistrazioneVolontarioPasswordNumeroMancante() throws IOException {
        var paese1 = mock(Paese.class);
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese1));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese1));
        when(paese1.getNome()).thenReturn("Italia");
        when(paese1.getId()).thenReturn(1);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1550, 830));
        driver.findElement(By.linkText("Iscriviti")).click();
        driver.findElement(By.id("firstNameRegistrationForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameRegistrationForm")).sendKeys("Squitieri");
        driver.findElement(By.id("emailRegistrationForm")).sendKeys("andrea.squitieri@mail.com");
        driver.findElement(By.id("passwordRegistrationForm")).sendKeys("Pwnns!cura");
        driver.findElement(By.id("confirmPasswordRegistrationForm")).sendKeys("Pwnns!cura");
        {
            WebElement dropdown = driver.findElement(By.id("nazionalitaRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Italia']")).click();
        }
        driver.findElement(By.id("dataNascitaRegistrationForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("numeroPassaportoRegistrationForm")).sendKeys("ASPASS");
        driver.findElement(By.id("dataScadenzaPassaportoRegistrationForm")).sendKeys("2099-04-01");
        driver.findElement(By.id("dataEmissionePassaportoRegistrationForm")).sendKeys("2010-04-01");
        driver.findElement(By.id("passaportoRegistrationForm")).sendKeys(getFilePath());
        {
            WebElement dropdown = driver.findElement(By.id("sessoRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Maschio']")).click();
        }
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback:nth-child(3)")).getText(), "La password deve contenere almeno un numero");
    }

    @Test
    public void TC1_16RegistrazioneVolontarioPasswordTroppoCorta() throws IOException {
        var paese1 = mock(Paese.class);
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese1));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese1));
        when(paese1.getNome()).thenReturn("Italia");
        when(paese1.getId()).thenReturn(1);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1550, 830));
        driver.findElement(By.linkText("Iscriviti")).click();
        driver.findElement(By.id("firstNameRegistrationForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameRegistrationForm")).sendKeys("Squitieri");
        driver.findElement(By.id("emailRegistrationForm")).sendKeys("andrea.squitieri@mail.com");
        driver.findElement(By.id("passwordRegistrationForm")).sendKeys("aB3!");
        driver.findElement(By.id("confirmPasswordRegistrationForm")).sendKeys("aB3!");
        {
            WebElement dropdown = driver.findElement(By.id("nazionalitaRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Italia']")).click();
        }
        driver.findElement(By.id("dataNascitaRegistrationForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("numeroPassaportoRegistrationForm")).sendKeys("ASPASS");
        driver.findElement(By.id("dataScadenzaPassaportoRegistrationForm")).sendKeys("2099-04-01");
        driver.findElement(By.id("dataEmissionePassaportoRegistrationForm")).sendKeys("2010-04-01");
        driver.findElement(By.id("passaportoRegistrationForm")).sendKeys(getFilePath());
        {
            WebElement dropdown = driver.findElement(By.id("sessoRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Maschio']")).click();
        }
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback:nth-child(3)")).getText(), "La password deve essere lunga almeno 8 caratteri");
    }

    @Test
    public void TC1_17RegistrazioneVolontarioNumeroPassaportoTroppoLungo() throws IOException {
        var paese1 = mock(Paese.class);
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese1));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese1));
        when(paese1.getNome()).thenReturn("Italia");
        when(paese1.getId()).thenReturn(1);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1550, 830));
        driver.findElement(By.linkText("Iscriviti")).click();
        driver.findElement(By.id("firstNameRegistrationForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameRegistrationForm")).sendKeys("Squitieri");
        driver.findElement(By.id("emailRegistrationForm")).sendKeys("andrea.squitieri@mail.com");
        driver.findElement(By.id("passwordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.id("confirmPasswordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        {
            WebElement dropdown = driver.findElement(By.id("nazionalitaRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Italia']")).click();
        }
        driver.findElement(By.id("dataNascitaRegistrationForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("numeroPassaportoRegistrationForm")).sendKeys("ASPASSAPORTO");
        driver.findElement(By.id("dataScadenzaPassaportoRegistrationForm")).sendKeys("2099-04-01");
        driver.findElement(By.id("dataEmissionePassaportoRegistrationForm")).sendKeys("2010-04-01");
        driver.findElement(By.id("passaportoRegistrationForm")).sendKeys(getFilePath());
        {
            WebElement dropdown = driver.findElement(By.id("sessoRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Maschio']")).click();
        }
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "Il numero del passaporto deve contenere al massimo 9 caratteri");
    }

    @Test
    public void TC1_18RegistrazioneVolontarioNumeroPassaportoCarattereSpecialeNonValido() throws IOException {
        var paese1 = mock(Paese.class);
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese1));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese1));
        when(paese1.getNome()).thenReturn("Italia");
        when(paese1.getId()).thenReturn(1);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1550, 830));
        driver.findElement(By.linkText("Iscriviti")).click();
        driver.findElement(By.id("firstNameRegistrationForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameRegistrationForm")).sendKeys("Squitieri");
        driver.findElement(By.id("emailRegistrationForm")).sendKeys("andrea.squitieri@mail.com");
        driver.findElement(By.id("passwordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.id("confirmPasswordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        {
            WebElement dropdown = driver.findElement(By.id("nazionalitaRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Italia']")).click();
        }
        driver.findElement(By.id("dataNascitaRegistrationForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("numeroPassaportoRegistrationForm")).sendKeys("ASPASS!");
        driver.findElement(By.id("dataScadenzaPassaportoRegistrationForm")).sendKeys("2099-04-01");
        driver.findElement(By.id("dataEmissionePassaportoRegistrationForm")).sendKeys("2010-04-01");
        driver.findElement(By.id("passaportoRegistrationForm")).sendKeys(getFilePath());
        {
            WebElement dropdown = driver.findElement(By.id("sessoRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Maschio']")).click();
        }
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "Il numero del passaporto deve contenere solo caratteri alfanumerici maiuscoli");
    }

    @Test
    public void TC1_19RegistrazioneVolontarioNumeroPassaportoCarattereMinuscoloNonValido() throws IOException {
        var paese1 = mock(Paese.class);
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese1));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese1));
        when(paese1.getNome()).thenReturn("Italia");
        when(paese1.getId()).thenReturn(1);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1550, 830));
        driver.findElement(By.linkText("Iscriviti")).click();
        driver.findElement(By.id("firstNameRegistrationForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameRegistrationForm")).sendKeys("Squitieri");
        driver.findElement(By.id("emailRegistrationForm")).sendKeys("andrea.squitieri@mail.com");
        driver.findElement(By.id("passwordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.id("confirmPasswordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        {
            WebElement dropdown = driver.findElement(By.id("nazionalitaRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Italia']")).click();
        }
        driver.findElement(By.id("dataNascitaRegistrationForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("numeroPassaportoRegistrationForm")).sendKeys("AsPASS");
        driver.findElement(By.id("dataScadenzaPassaportoRegistrationForm")).sendKeys("2099-04-01");
        driver.findElement(By.id("dataEmissionePassaportoRegistrationForm")).sendKeys("2010-04-01");
        driver.findElement(By.id("passaportoRegistrationForm")).sendKeys(getFilePath());
        {
            WebElement dropdown = driver.findElement(By.id("sessoRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Maschio']")).click();
        }
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "Il numero del passaporto deve contenere solo caratteri alfanumerici maiuscoli");
    }

    @Test
    public void TC1_20RegistrazioneVolontarioFilePassaportoNonValido() throws IOException {
        var paese1 = mock(Paese.class);
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(List.of(paese1));
        when(paeseRepository.findById(1)).thenReturn(Optional.of(paese1));
        when(paese1.getNome()).thenReturn("Italia");
        when(paese1.getId()).thenReturn(1);

        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1550, 830));
        driver.findElement(By.linkText("Iscriviti")).click();
        driver.findElement(By.id("firstNameRegistrationForm")).sendKeys("Andrea");
        driver.findElement(By.id("lastNameRegistrationForm")).sendKeys("Squitieri");
        driver.findElement(By.id("emailRegistrationForm")).sendKeys("andrea.squitieri@mail.com");
        driver.findElement(By.id("passwordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        driver.findElement(By.id("confirmPasswordRegistrationForm")).sendKeys("PasswordMoltoSicura1234!");
        {
            WebElement dropdown = driver.findElement(By.id("nazionalitaRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Italia']")).click();
        }
        driver.findElement(By.id("dataNascitaRegistrationForm")).sendKeys("2004-04-01");
        driver.findElement(By.id("numeroPassaportoRegistrationForm")).sendKeys("ASPASS");
        driver.findElement(By.id("dataScadenzaPassaportoRegistrationForm")).sendKeys("2099-04-01");
        driver.findElement(By.id("dataEmissionePassaportoRegistrationForm")).sendKeys("2010-04-01");
        driver.findElement(By.id("passaportoRegistrationForm")).sendKeys(getWebpFilePath());
        {
            WebElement dropdown = driver.findElement(By.id("sessoRegistrationForm"));
            dropdown.findElement(By.xpath("//option[. = 'Maschio']")).click();
        }
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".invalid-feedback")).getText(), "Tipo di file non valido");
    }

}
