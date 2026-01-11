package com.earthlocals.earthlocals.model;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);


    Optional<PasswordResetToken> findByUtente(Utente utente);
}
