package com.earthlocals.earthlocals.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UtenteRepository extends CrudRepository<Utente, Long>, UserDetailsService{
    Utente findByEmail(String email);

    @Override
    default UserDetails loadUserByUsername(String userrname) {
        return this.findByEmail(userrname);
    }
}
