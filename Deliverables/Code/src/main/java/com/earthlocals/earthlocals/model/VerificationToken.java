package com.earthlocals.earthlocals.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class VerificationToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String token;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    private Utente utente;

    private LocalDateTime expiryDate;

    public VerificationToken(Utente utente, String token) {
        this.utente = utente;
        this.token = token;
        this.expiryDate = LocalDateTime.now().plusMinutes(EXPIRATION);
    }

}
