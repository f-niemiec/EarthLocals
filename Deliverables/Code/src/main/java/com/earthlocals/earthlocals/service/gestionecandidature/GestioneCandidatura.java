package com.earthlocals.earthlocals.service.gestionecandidature;

import com.earthlocals.earthlocals.model.*;
import com.earthlocals.earthlocals.service.gestionecandidature.dto.CandidaturaDTO;
import com.earthlocals.earthlocals.service.gestionecandidature.exceptions.CandidaturaAlreadyExistsException;
import com.earthlocals.earthlocals.service.gestionecandidature.exceptions.CandidaturaNotAcceptableException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class GestioneCandidatura {

    final private CandidaturaRepository candidaturaRepository;
    final private MissioneRepository missioneRepository;
    final private VolontarioRepository volontarioRepository;

    public Candidatura registerCandidatura(CandidaturaDTO candidaturaDTO) {
        Missione missione = missioneRepository
                .findById(candidaturaDTO.getMissioneId())
                .orElseThrow(() -> new IllegalArgumentException("Missione non trovata"));
        if (!(missione.getStato().equals(Missione.MissioneStato.ACCETTATA))) {
            throw new CandidaturaNotAcceptableException();
        }
        Volontario candidato = volontarioRepository
                .findById(candidaturaDTO.getCandidatoId())
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));


        if (this.hasVolontarioAlreadyApplied(missione, candidato)) {
            throw new CandidaturaAlreadyExistsException();
        }

        Candidatura candidatura = new Candidatura();
        candidatura.setMissione(missione);
        candidatura.setCandidato(candidato);
        candidatura.setDataCandidatura(LocalDate.now());
        candidatura.setStato(Candidatura.CandidaturaStato.IN_CORSO);

        candidaturaRepository.save(candidatura);
        return candidatura;
    }

    public boolean hasVolontarioAlreadyApplied(CandidaturaDTO dto) {
        Missione missione = missioneRepository
                .findById(dto.getMissioneId())
                .orElseThrow(() -> new IllegalArgumentException("Missione non trovata"));
        Volontario candidato = volontarioRepository
                .findById(dto.getCandidatoId())
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        return candidaturaRepository.existsByMissioneAndCandidato(missione, candidato);
    }

    public boolean hasVolontarioAlreadyApplied(Missione missione, Volontario candidato) {
        return candidaturaRepository.existsByMissioneAndCandidato(missione, candidato);
    }

    public void removeCandidatura(CandidaturaDTO candidaturaDTO) {
        Missione missione = missioneRepository
                .findById(candidaturaDTO.getMissioneId())
                .orElseThrow(() -> new IllegalArgumentException("Missione non trovata"));
        //Idem
        Volontario candidato = volontarioRepository
                .findById(candidaturaDTO.getCandidatoId())
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        if (!hasVolontarioAlreadyApplied(missione, candidato)) {
            return;
        }
        Candidatura candidatura = candidaturaRepository.findByMissioneAndCandidato(missione, candidato);
        candidaturaRepository.delete(candidatura);
    }

    public boolean acceptCandidatura(Long id) {
        var candidatura = candidaturaRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Candidatura non trovata"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Utente utente = (Utente) auth.getPrincipal();
        if (!Objects.equals(candidatura.getMissione().getCreatore().getId(), utente.getId())) {
            return false;
        }
        candidatura.setStato(Candidatura.CandidaturaStato.ACCETTATA);
        return true;
    }

    public boolean rejectCandidatura(Long id) {
        var candidatura = candidaturaRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Candidatura non trovata"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Utente utente = (Utente) auth.getPrincipal();
        if (!Objects.equals(candidatura.getMissione().getCreatore().getId(), utente.getId())) {
            return false;
        }
        candidatura.setStato(Candidatura.CandidaturaStato.RIFIUTATA);
        return true;
    }

    @PreAuthorize("hasRole('VOLUNTEER')")
    public Page<Candidatura> getCandidatureVolontario(Integer page, Integer pageSize) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Volontario volontario = (Volontario) auth.getPrincipal();
        var pageable = Pageable.ofSize(pageSize).withPage(page);
        return candidaturaRepository.findByCandidato(volontario, pageable);
    }

    public Page<Candidatura> getEsperienzeVolontario(Integer page, Integer pageSize) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Utente utente = (Utente) auth.getPrincipal();
        var pageable = Pageable.ofSize(pageSize).withPage(page);
        return candidaturaRepository.findByCandidatoAndStatoAndMissioneInternalStatoAndMissioneDataFineBefore(
                utente,
                Candidatura.CandidaturaStato.ACCETTATA,
                Missione.InternalMissioneStato.ACCETTATA,
                LocalDate.now(),
                pageable);

    }

    public Page<Candidatura> getRichiesteCandidatura(Integer page, Integer pageSize) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Utente utente = (Utente) auth.getPrincipal();
        var pageable = Pageable.ofSize(pageSize).withPage(page);
        return candidaturaRepository.findByOrganizzatore(utente, pageable);
    }

}
