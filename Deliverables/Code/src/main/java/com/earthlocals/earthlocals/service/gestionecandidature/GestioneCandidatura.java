package com.earthlocals.earthlocals.service.gestionecandidature;

import com.earthlocals.earthlocals.model.*;
import com.earthlocals.earthlocals.service.gestionecandidature.dto.CandidaturaDTO;
import com.earthlocals.earthlocals.service.gestionecandidature.exceptions.CandidaturaAlreadyExistsException;
import com.earthlocals.earthlocals.service.gestionecandidature.exceptions.CandidaturaNotAcceptableException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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

    private boolean hasVolontarioAlreadyApplied(Missione missione, Volontario candidato) {
        return candidaturaRepository.existsByMissioneAndCandidato(missione, candidato);
    }
}