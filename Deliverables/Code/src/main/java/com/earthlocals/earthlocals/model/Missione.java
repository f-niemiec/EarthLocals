package com.earthlocals.earthlocals.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "missioneBuilder")
public class Missione implements Serializable {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @ManyToOne(optional = false)
    private Paese paese;

    @Column(nullable = false)
    private String citta;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descrizione;

    @Column(nullable = false)
    private LocalDate dataInizio;

    @Column(nullable = false)
    private LocalDate dataFine;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String competenzeRichieste;

    @Column(columnDefinition = "TEXT")
    private String requisitiExtra;

    @Column(nullable = false)
    private String immagine;

    @Column(nullable = false)
    private MissioneStato stato;

    @OneToMany(mappedBy = "missione")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Candidatura> candidature;

    @Column(nullable = false)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private InternalMissioneStato internalStato;

    @ManyToOne(optional = false)
    private Utente creatore;

    public MissioneStato getStato() {
        switch (this.internalStato) {
            case PENDING -> {
                return MissioneStato.PENDING;
            }
            case RIFIUTATA -> {
                return MissioneStato.RIFIUTATA;
            }
            case ACCETTATA -> {
                if (dataFine.isBefore(LocalDate.now())) {
                    return MissioneStato.COMPLETATA;
                } else {
                    return MissioneStato.ACCETTATA;
                }
            }
        }
        return null;
    }


    public boolean accettaMissione() {
        if (internalStato.equals(InternalMissioneStato.PENDING)) {
            internalStato = InternalMissioneStato.ACCETTATA;
            return true;
        }
        return false;
    }

    public boolean rifiutaMissione() {
        if (internalStato.equals(InternalMissioneStato.PENDING)) {
            internalStato = InternalMissioneStato.RIFIUTATA;
            return true;
        }
        return false;
    }


    public enum InternalMissioneStato {
        PENDING,
        RIFIUTATA,
        ACCETTATA,
    }


    public enum MissioneStato {
        PENDING,
        RIFIUTATA,
        ACCETTATA,
        COMPLETATA,
    }


}
