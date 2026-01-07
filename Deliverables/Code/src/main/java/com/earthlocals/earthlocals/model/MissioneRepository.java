package com.earthlocals.earthlocals.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MissioneRepository extends JpaRepository<Missione, Long> {
    List<Missione> findByCreatore(Utente creatore);

    Page<Missione> findByPaese(Paese paese, Pageable pageable);

    Page<Missione> findByCreatore(Utente creatore, Pageable pageable);

    Page<Missione> findByStato(Missione.MissioneStato stato, Pageable pageable);

    Page<Missione> findByPaeseAndStato(Paese paese, Missione.MissioneStato stato, Pageable pageable);

}
