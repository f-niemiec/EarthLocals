package com.earthlocals.earthlocals.service.gestioneutente;

import com.earthlocals.earthlocals.model.*;
import com.earthlocals.earthlocals.service.gestioneutente.dto.*;
import com.earthlocals.earthlocals.service.gestioneutente.exception.UserAlreadyExistsException;
import com.earthlocals.earthlocals.service.gestioneutente.passport.PassportStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collections;

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

    public Utente registerVolunteer(VolontarioDTO volontarioDTO) throws UserAlreadyExistsException {
        if (emailExists(volontarioDTO.getEmail())) {
            throw new UserAlreadyExistsException();
        }
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
        utenteBuilder.pending(false); // TODO: Conferma registrazione
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
        if (emailExists(utenteDTO.getEmail())) {
            throw new UserAlreadyExistsException();
        }
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
        utenteBuilder.pending(false); // TODO: Conferma registrazione
        utenteBuilder.ruoli(Collections.singletonList(ruolo));

        var utente = utenteBuilder.build();

        utenteRepository.save(utente);
        return utente;
    }

    public Utente editUser(Utente utente, EditUtenteDTO editUtenteDTO) {
        Paese p = paeseRepository.findById(editUtenteDTO.getNazionalita()).orElseThrow();
        utente.setNome(editUtenteDTO.getNome());
        utente.setCognome(editUtenteDTO.getCognome());
        utente.setEmail(editUtenteDTO.getEmail());
        utente.setDataNascita(editUtenteDTO.getDataNascita());
        utente.setSesso(editUtenteDTO.getSesso());
        utente.setNazionalita(p);
        return utenteRepository.save(utente);
    }


    private boolean emailExists(String email) {
        return utenteRepository.findByEmail(email) != null;
    }


    @PreAuthorize("hasRole('VOLUNTEER')")
    public InputStream getPassportVolontario() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Volontario volontario = (Volontario) auth.getPrincipal();
        return passportStorageService.downloadFile(volontario.getPathPassaporto());
    }

    @PreAuthorize("hasRole('VOLUNTEER')")
    public FileSystemResource getPassportVolontarioFileResource() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Volontario volontario = (Volontario) auth.getPrincipal();
        return passportStorageService.downloadFileResource(volontario.getPathPassaporto());
    }

    //TODO Logica per cambio password (DTO a parte o EditUtente) (high-priority)
    //TODO Logica per cambio passaporto (forse DTO a parte)(low-priority)

}
