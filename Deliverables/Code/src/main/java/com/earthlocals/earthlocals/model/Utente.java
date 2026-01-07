package com.earthlocals.earthlocals.model;

import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "utenteBuilder")
public class Utente implements Serializable, UserDetails {

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

    @ManyToOne
    private Paese nazionalita;

    @Column(nullable = false)
    private Boolean pending;

    private String bio;

    private String fotoProfilo;

    private String tempPassword;

    private Date tempPwdScadenza;

    @OneToMany(mappedBy = "autore")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Recensione> recensioniScritte;

    @OneToMany(mappedBy = "destinatario")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Recensione> recensioniRicevute;


    @ManyToMany(fetch = FetchType.EAGER)
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


    @Override
    @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.ruoli;
    }

    @Override
    @NullMarked
    public String getUsername() {
        return this.email;

    }


    @Override
    public boolean isEnabled() {
        return !this.pending;
    }
}
