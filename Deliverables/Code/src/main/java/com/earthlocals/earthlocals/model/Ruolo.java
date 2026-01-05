package com.earthlocals.earthlocals.model;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ruolo implements Serializable{

    public static final String ACCOUNT_MANAGER = "ROLE_ACCOUNT_MANAGER";
    public static final String VOLUNTEER = "ROLE_VOLUNTEER";
    public static final String ORGANIZER = "ROLE_ORGANIZER";
    public static final String MODERATOR = "ROLE_MODERATOR";

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @ManyToMany(mappedBy = "ruoli")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Collection<Utente> utenti;

    public Ruolo(String nome) {
        this.nome = nome;
    }
}
