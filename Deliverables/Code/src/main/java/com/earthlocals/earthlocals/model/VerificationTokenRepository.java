package com.earthlocals.earthlocals.model;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    VerificationToken findByUtente(Utente utente);
}
