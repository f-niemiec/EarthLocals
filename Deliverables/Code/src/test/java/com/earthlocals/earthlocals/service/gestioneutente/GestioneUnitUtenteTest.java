package com.earthlocals.earthlocals.service.gestioneutente;

import com.earthlocals.earthlocals.model.*;
import com.earthlocals.earthlocals.service.gestioneutente.passport.PassportStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class GestioneUnitUtenteTest {
    @Mock
    private VolontarioRepository volontarioRepository;
    @Mock
    private UtenteRepository utenteRepository;
    @Mock
    private RuoloRepository ruoloRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PassportStorageService passportStorageService;
    @Mock
    private PaeseRepository paeseRepository;
    @Mock
    private VerificationTokenRepository verificationTokenRepository;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @InjectMocks
    private GestioneUtente gestioneUtente;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

}
