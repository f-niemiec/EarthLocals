package com.earthlocals.earthlocals.service.gestioneutente;

import com.earthlocals.earthlocals.model.*;
import com.earthlocals.earthlocals.service.gestioneutente.dto.*;
import com.earthlocals.earthlocals.service.gestioneutente.exceptions.UserAlreadyExistsException;
import com.earthlocals.earthlocals.service.gestioneutente.exceptions.WrongPasswordException;
import com.earthlocals.earthlocals.service.gestioneutente.passport.PassportStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class GestioneUtente {

    final private VolontarioRepository volontarioRepository;
    final private UtenteRepository utenteRepository;
    final private RuoloRepository ruoloRepository;
    final private PasswordEncoder passwordEncoder;
    final private PassportStorageService passportStorageService;
    final private PaeseRepository paeseRepository;
    final private VerificationTokenRepository verificationTokenRepository;

    public Utente registerVolunteer(VolontarioDTO volontarioDTO) throws UserAlreadyExistsException {
        checkUserExists(volontarioDTO.getEmail());
        Paese p = paeseRepository.findById(volontarioDTO.getNazionalita()).orElseThrow();
        Ruolo ruolo = ruoloRepository.findByNome(Ruolo.VOLUNTEER);

        var utenteBuilder = Volontario.volontarioBuilder();

        utenteBuilder.nome(volontarioDTO.getNome());
        utenteBuilder.cognome(volontarioDTO.getCognome());
        utenteBuilder.email(volontarioDTO.getEmail());
        utenteBuilder.password(passwordEncoder.encode(volontarioDTO.getPassword()));
        utenteBuilder.dataNascita(volontarioDTO.getDataNascita());
        utenteBuilder.sesso(volontarioDTO.getSesso());
        utenteBuilder.nazionalita(p);
        utenteBuilder.pending(true);
        utenteBuilder.ruoli(Collections.singletonList(ruolo));
        utenteBuilder.numeroPassaporto(volontarioDTO.getNumeroPassaporto());
        utenteBuilder.dataScadenzaPassaporto(volontarioDTO.getDataScadenzaPassaporto());
        utenteBuilder.dataEmissionePassaporto(volontarioDTO.getDataEmissionePassaporto());
        try {
            utenteBuilder.pathPassaporto(passportStorageService.acceptUpload(volontarioDTO.getPassaporto()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        var utente = utenteBuilder.build();

        utenteRepository.save(utente);
        return utente;
    }

    public Utente registerOrganizer(UtenteDTO utenteDTO) throws UserAlreadyExistsException {
        checkUserExists(utenteDTO.getEmail());

        Paese p = paeseRepository.findById(utenteDTO.getNazionalita()).orElseThrow();
        Ruolo ruolo = ruoloRepository.findByNome(Ruolo.ORGANIZER);

        // TODO: Evitare ripetizione codice
        var utenteBuilder = Utente.utenteBuilder();

        utenteBuilder.nome(utenteDTO.getNome());
        utenteBuilder.cognome(utenteDTO.getCognome());
        utenteBuilder.email(utenteDTO.getEmail());
        utenteBuilder.password(passwordEncoder.encode(utenteDTO.getPassword()));
        utenteBuilder.dataNascita(utenteDTO.getDataNascita());
        utenteBuilder.sesso(utenteDTO.getSesso());
        utenteBuilder.nazionalita(p);
        utenteBuilder.pending(true);
        utenteBuilder.ruoli(Collections.singletonList(ruolo));

        var utente = utenteBuilder.build();

        utenteRepository.save(utente);
        return utente;
    }

    public Utente editUser(EditUtenteDTO editUtenteDTO) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var utente = (Utente) auth.getPrincipal();
        var p = paeseRepository.findById(editUtenteDTO.getNazionalita()).orElseThrow();

        utente.setNome(editUtenteDTO.getNome());
        utente.setCognome(editUtenteDTO.getCognome());
        utente.setEmail(editUtenteDTO.getEmail());
        utente.setDataNascita(editUtenteDTO.getDataNascita());
        utente.setSesso(editUtenteDTO.getSesso());
        utente.setNazionalita(p);
        return utenteRepository.save(utente);
    }

    public Utente editPassword(EditPasswordDTO editPasswordDTO) throws WrongPasswordException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var utente = (Utente) auth.getPrincipal();
        if (!passwordEncoder.matches(editPasswordDTO.getCurrentPassword(), utente.getPassword())) {
            throw new WrongPasswordException();
        }
        utente.setPassword(passwordEncoder.encode(editPasswordDTO.getNewPassword()));
        return utenteRepository.save(utente);
    }

    public Volontario editPassport(EditPassportDTO editPassportDTO) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var volontario = (Volontario) auth.getPrincipal();
        volontario.setNumeroPassaporto(editPassportDTO.getNumeroPassaporto());
        volontario.setDataScadenzaPassaporto(editPassportDTO.getDataScadenzaPassaporto());
        volontario.setDataEmissionePassaporto(editPassportDTO.getDataEmissionePassaporto());
        passportStorageService.removeFile(volontario.getPathPassaporto());
        try {
            volontario.setPathPassaporto(passportStorageService.acceptUpload(editPassportDTO.getPassaporto()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return volontarioRepository.save(volontario);
    }

    private void checkUserExists(String email) throws UserAlreadyExistsException {
        var user = utenteRepository.findByEmail(email);
        if (user == null) {
            return;
        }
        if (!user.getPending()) {
            throw new UserAlreadyExistsException();
        }
        //TODO
        utenteRepository.delete(user);
    }

    @PreAuthorize("hasRole('VOLUNTEER')")
    public FileSystemResource getPassportVolontarioFileResource() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Volontario volontario = (Volontario) auth.getPrincipal();
        return passportStorageService.downloadFile(volontario.getPathPassaporto());
    }

    public String createVerificationToken(Utente utente) {
        var token = UUID.randomUUID().toString();
        var verToken = new VerificationToken(utente, token);
        verificationTokenRepository.save(verToken);
        return verToken.getToken();
    }

    public void activateAccount(String token) {
        var verToken = verificationTokenRepository.findByToken(token).orElseThrow();
        var utente = verToken.getUtente();
        if (verToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token scaduto");
        }
        utente.setPending(false);
        utenteRepository.save(utente);
        verificationTokenRepository.delete(verToken);
    }
}
