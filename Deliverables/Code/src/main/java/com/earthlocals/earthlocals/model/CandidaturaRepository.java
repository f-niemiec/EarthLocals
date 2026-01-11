package com.earthlocals.earthlocals.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface CandidaturaRepository extends CrudRepository<Candidatura, Long> {
    Page<Candidatura> findByCandidato(Volontario volontario, Pageable pageable);

    Page<Candidatura> findByStatoAndCandidato(Candidatura.CandidaturaStato candidaturaStato, Utente utente, Pageable pageable);

    @Query("SELECT c FROM Candidatura c JOIN c.missione m WHERE m.creatore = ?1")
    Page<Candidatura> findByOrganizzatore(Utente utente, Pageable pageable);

    @Query("SELECT c FROM Candidatura c JOIN c.missione m WHERE c.candidato = :candidato AND c.stato = :stato AND m.internalStato = :missioneStato AND m.dataFine <= :data")
    Page<Candidatura> findByCandidatoAndStatoAndMissioneInternalStatoAndMissioneDataFineBefore(
            @Param("candidato") Utente candidato,
            @Param("stato") Candidatura.CandidaturaStato stato,
            @Param("missioneStato") Missione.InternalMissioneStato missioneStato,
            @Param("data") LocalDate data,
            Pageable pageable);
    
    boolean existsByMissioneAndCandidato(Missione missione, Volontario candidato);

    Candidatura findByMissioneAndCandidato(Missione missione, Volontario volontario);

}
