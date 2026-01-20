package com.earthlocals.earthlocals.service.gestionemissioni;

import com.earthlocals.earthlocals.model.*;
import com.earthlocals.earthlocals.service.gestionemissioni.dto.MissioneDTO;
import com.earthlocals.earthlocals.service.gestionemissioni.exceptions.MissioneNotAcceptableException;
import com.earthlocals.earthlocals.service.gestionemissioni.exceptions.MissioneNotFoundException;
import com.earthlocals.earthlocals.service.gestionemissioni.pictures.PicturesStorageService;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class GestioneMissione {

    final private Validator validator;
    final private MissioneRepository missioneRepository;
    final private PicturesStorageService storageService;
    final private PaeseRepository paeseRepository;

    @PreAuthorize("hasRole('ORGANIZER')")
    public Missione registerMissione(MissioneDTO missioneDTO) throws Exception {
        var constraintViolation = validator.validate(missioneDTO);
        if (!constraintViolation.isEmpty()) {
            throw new ConstraintViolationException(constraintViolation);
        }

        String fileName = storageService.acceptUpload(missioneDTO.getFoto());
        Paese paese = paeseRepository.findById(missioneDTO.getPaese()).orElseThrow();
        var missioneBuilder = Missione.missioneBuilder()
                .dataFine(missioneDTO.getDataFine())
                .creatore(missioneDTO.getCreatore())
                .citta(missioneDTO.getCitta())
                .competenzeRichieste(missioneDTO.getCompetenzeRichieste())
                .dataInizio(missioneDTO.getDataInizio())
                .descrizione(missioneDTO.getDescrizione())
                .immagine(fileName)
                .nome(missioneDTO.getNome())
                .paese(paese);

        if (missioneDTO.getRequisitiExtra() != null) {
            missioneBuilder.requisitiExtra(missioneDTO.getRequisitiExtra());
        }

        var missione = missioneBuilder.build();

        missioneRepository.save(missione);
        return missione;
    }

    @PreAuthorize("hasRole('MODERATOR')")
    public boolean acceptMissione(Long id) throws MissioneNotFoundException {
        var missione = missioneRepository.findById(id).orElseThrow(MissioneNotFoundException::new);
        if (!missione.getStato().equals(Missione.MissioneStato.PENDING)) {
            throw new MissioneNotAcceptableException();
        }
        missione.accettaMissione();
        return true;
    }

    @PreAuthorize("hasRole('MODERATOR')")
    public boolean rejectMissione(Long id) throws MissioneNotFoundException {
        var missione = missioneRepository.findById(id).orElseThrow(MissioneNotFoundException::new);
        if (!missione.getStato().equals(Missione.MissioneStato.PENDING)) {
            //Forse opportuno definirne un'altra
            throw new MissioneNotAcceptableException();
        }
        missione.rifiutaMissione();
        return true;
    }


    public Page<Missione> getMissioniAperte(Integer paeseId, int pageNumber, int pageSize) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("pageNumber cannot be negative");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be positive");
        }
        final var statoAperto = Missione.InternalMissioneStato.ACCETTATA;
        // Creiamo l'oggetto per la paginazione
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("dataInizio").ascending());

        var now = LocalDate.now();


        if (paeseId == null || paeseId == 0) {
            return missioneRepository.findByInternalStatoAndDataFineAfter(statoAperto, now, pageable);
        } else {
            Paese paese = paeseRepository.findById(paeseId).orElse(null);
            if (paese == null) {
                return missioneRepository.findByInternalStatoAndDataFineAfter(statoAperto, now, pageable);
            }
            return missioneRepository.findByPaeseAndInternalStatoAndDataFineAfter(paese, statoAperto, now, pageable);
        }
    }

    @PreAuthorize("hasRole('MODERATOR')")
    public Page<Missione> getMissioniPending(int pageNumber, int pageSize) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("pageNumber cannot be negative");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be positive");
        }
        final var stato = Missione.InternalMissioneStato.PENDING;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("dataInizio").ascending());
        var now = LocalDate.now();
        return missioneRepository.findByInternalStatoAndDataFineAfter(stato, now, pageable);
    }


    @PreAuthorize("hasRole('ORGANIZER')")
    public Page<Missione> getMissioniOrganizzatore(int page, int pageSize) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Utente utente = (Utente) auth.getPrincipal();


        var pageable = Pageable.ofSize(pageSize).withPage(page);

        return missioneRepository.findByCreatore(utente, pageable);
    }


    public Resource getImmagineMissione(String immagine) {
        try {
            return storageService.downloadFile(immagine);
        } catch (Exception e) {
            return new ClassPathResource("static/resources/images/placeholder.jpg");
        }
    }


    public Missione getMissioneById(Long id) throws MissioneNotFoundException {
        var missione = missioneRepository.findById(id).orElseThrow(MissioneNotFoundException::new);
        return missione;
    }

}