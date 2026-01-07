package com.earthlocals.earthlocals.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
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

    @Column(nullable = false)
    @Lob
    private String descrizione;

    @Column(nullable = false)
    private Date dataInizio;

    @Column(nullable = false)
    private Date dataFine;

    @Column(nullable = false)
    @Lob
    private String competenzeRichieste;

    @Lob
    private String requisitiExtra;

    //Sinceramente dubbioso su come gestirla
    @Column(nullable = false)
    private String immagine;

    @Column(nullable = false)
    private MissioneStato stato;

    @OneToMany(mappedBy = "missione")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Candidatura> candidature;


    @ManyToOne(optional = false)
    private Utente creatore;

    public enum MissioneStato {
        IN_CORSO,
        COMPLETATA,
        ANNULLATA,
        PENDING,
    }

}
