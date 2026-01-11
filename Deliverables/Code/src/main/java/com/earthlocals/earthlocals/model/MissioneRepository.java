package com.earthlocals.earthlocals.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MissioneRepository extends JpaRepository<Missione, Long> {
    List<Missione> findByCreatore(Utente creatore);

    Page<Missione> findByPaese(Paese paese, Pageable pageable);

    Page<Missione> findByCreatore(Utente creatore, Pageable pageable);

    Page<Missione> findByStato(Missione.MissioneStato stato, Pageable pageable);

    Page<Missione> findByPaeseAndStato(Paese paese, Missione.MissioneStato stato, Pageable pageable);

    Page<Missione> findByInternalStatoAndDataFineAfter(Missione.InternalMissioneStato stato, LocalDate date, Pageable pageable);

    Page<Missione> findByPaeseAndInternalStatoAndDataFineAfter(Paese paese, Missione.InternalMissioneStato stato, LocalDate date, Pageable pageable);

}
