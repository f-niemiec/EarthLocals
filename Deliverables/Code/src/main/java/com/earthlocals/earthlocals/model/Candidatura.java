package com.earthlocals.earthlocals.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candidatura implements Serializable {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Long id;
    private Date dataCandidatura;

    @Enumerated(EnumType.STRING)
    private CandidaturaStato stato;

    @ManyToOne(optional = false)
    private Volontario candidato;

    @ManyToOne(optional = false)
    private Missione missione;

    public enum CandidaturaStato {
        IN_CORSO, ACCETTATA, RIFIUTATA
    }

}
