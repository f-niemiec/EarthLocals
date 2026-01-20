package com.earthlocals.earthlocals.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UtenteRepository extends CrudRepository<Utente, Long>, UserDetailsService {
    Utente findByEmail(String email);

    @Override
    default UserDetails loadUserByUsername(String userrname) {
        var res = this.findByEmail(userrname);
        if (res == null) {
            throw new UsernameNotFoundException("Utente non trovato");
        }
        return res;
    }
}
