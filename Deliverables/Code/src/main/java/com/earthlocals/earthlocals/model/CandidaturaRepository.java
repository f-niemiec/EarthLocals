package com.earthlocals.earthlocals.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface CandidaturaRepository extends CrudRepository<Candidatura, Long> {
    Page<Candidatura> findByCandidato(Volontario volontario, Pageable pageable);

    Page<Candidatura> findByStatoAndCandidato(Candidatura.CandidaturaStato candidaturaStato, Utente utente, Pageable pageable);
}
