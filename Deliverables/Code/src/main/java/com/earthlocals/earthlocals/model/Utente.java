package com.earthlocals.earthlocals.model;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "utenteBuilder")
public class Utente implements Serializable{

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Long id = null;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cognome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Date dataNascita;

    @Column(nullable = false)
    private Character sesso;

    @Column(nullable = false)
    private String nazionalita;

    @Column(nullable = false)
    private Boolean pending;

    private String bio;

    private String fotoProfilo;

    private String tempPassword;

    private Date tempPwdScadenza;

    //TODO recensioniScritte
    //TODO recensioniRicevute
    //TODO candidature
    //TODO gestione ruolo volontario


    @ManyToMany
    @JoinTable(
            name = "ruoli_utenti",
            joinColumns = @JoinColumn(
                    name = "utente_id", referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "ruolo_id", referencedColumnName = "id"
            )
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Collection<Ruolo> ruoli;

}
