package com.earthlocals.earthlocals.service.gestionepaese;

import com.earthlocals.earthlocals.model.Paese;
import com.earthlocals.earthlocals.model.PaeseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GestionePaese {

    final private PaeseRepository paeseRepository;

    public List<Paese> getPaesiSortedByName(Sort.Direction direction) {
        Sort sort = Sort.by(direction, "nome");
        return paeseRepository.findAll(sort);
    }

}
