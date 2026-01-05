package com.earthlocals.earthlocals.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UtenteRepository extends CrudRepository<Utente, Long>{
    Utente findByEmail(String email);
}
