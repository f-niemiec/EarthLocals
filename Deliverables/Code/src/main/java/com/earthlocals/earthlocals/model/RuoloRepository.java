package com.earthlocals.earthlocals.model;

import org.springframework.data.repository.CrudRepository;

public interface RuoloRepository extends CrudRepository<Ruolo, Long> {
    Ruolo findByNome(String nome);
}
