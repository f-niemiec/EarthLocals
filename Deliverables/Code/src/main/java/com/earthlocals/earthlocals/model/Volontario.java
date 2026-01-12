package com.earthlocals.earthlocals.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
public class Volontario extends Utente {

    private String numeroPassaporto;
    private LocalDate dataScadenzaPassaporto;
    private LocalDate dataEmissionePassaporto;
    private String pathPassaporto;

    @OneToMany(mappedBy = "candidato")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Candidatura> candidature;


    @Builder(builderMethodName = "volontarioBuilder")
    public Volontario(Long id, String nome, String cognome, String email, String password, LocalDate dataNascita, Character sesso, Paese nazionalita, Boolean pending, String bio, String fotoProfilo, Set<Recensione> recensioniScritte, Set<Recensione> recensioniRicevute, Set<Candidatura> candidature, String numeroPassaporto, LocalDate dataScadenzaPassaporto, LocalDate dataEmissionePassaporto, String pathPassaporto, Collection<Ruolo> ruoli) {
        super(id, nome, cognome, email, password, dataNascita, sesso, nazionalita, pending, bio, fotoProfilo, recensioniScritte, recensioniRicevute, ruoli);
        this.numeroPassaporto = numeroPassaporto;
        this.dataScadenzaPassaporto = dataScadenzaPassaporto;
        this.dataEmissionePassaporto = dataEmissionePassaporto;
        this.pathPassaporto = pathPassaporto;
        this.candidature = candidature;
    }


}
