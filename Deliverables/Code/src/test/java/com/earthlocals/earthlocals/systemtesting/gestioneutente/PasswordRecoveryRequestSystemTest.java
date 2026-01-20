package com.earthlocals.earthlocals.systemtesting.gestioneutente;


import com.earthlocals.earthlocals.config.SystemTestAppConfig;
import com.earthlocals.earthlocals.config.TestcontainerConfig;
import com.earthlocals.earthlocals.model.Utente;
import com.earthlocals.earthlocals.model.UtenteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.http.server.LocalTestWebServer;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.context.WebApplicationContext;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SystemTestAppConfig.class, TestcontainerConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PasswordRecoveryRequestSystemTest {

    JavascriptExecutor js;
    @LocalServerPort
    private int port;
    private WebDriver driver;
    private Map<String, Object> vars;
    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private ApplicationContext context;

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

    @Test
    public void TC6_1RichiestaRecuperoPasswordEmailAssociata() {
        var utente = Utente.utenteBuilder()
                .nome("Mario")
                .cognome("Rossi")
                .email("john.travolta42@email.it")
                .password("PasswordMoltoSicura1234!")
                .dataNascita(LocalDate.of(2004, 4, 1))
                .sesso('M')
                .pending(false)
                .build();
        utenteRepository.save(utente);
        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1550, 830));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.linkText("Password dimenticata?")).click();
        driver.findElement(By.id("inputEmailResetPasswordForm")).sendKeys("john.travolta42@email.it");
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".alert")).getText(), "Ti abbiamo inviato una mail\nProcedi al recupero della password accedendo attraverso il link che trovi nella tua casella di posta elettronica.");
    }

    @Test
    public void TC6_2RichiestaRecuperoPasswordEmailNonAssociata() {
        driver.get(LocalTestWebServer.obtain(this.context).uri());
        driver.manage().window().setSize(new Dimension(1550, 830));
        driver.findElement(By.linkText("Log in")).click();
        driver.findElement(By.linkText("Password dimenticata?")).click();
        driver.findElement(By.id("inputEmailResetPasswordForm")).sendKeys("john.travolta50@email.it");
        driver.findElement(By.cssSelector(".btn")).click();
        assertEquals(driver.findElement(By.cssSelector(".alert")).getText(), "Ti abbiamo inviato una mail\nProcedi al recupero della password accedendo attraverso il link che trovi nella tua casella di posta elettronica.");
    }
}
