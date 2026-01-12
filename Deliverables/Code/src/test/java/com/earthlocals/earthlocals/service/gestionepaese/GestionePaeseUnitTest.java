package com.earthlocals.earthlocals.service.gestionepaese;

import com.earthlocals.earthlocals.model.Paese;
import com.earthlocals.earthlocals.model.PaeseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GestionePaeseUnitTest {

    @InjectMocks
    private GestionePaese gestionePaese;

    @Mock
    private PaeseRepository paeseRepository;

    @Test
    void getPaesiSortedByNameDesc() {
        List<Paese> wrongList = List.of(
                new Paese(1, "Francia")
        );
        List<Paese> oracleList = List.of(
                new Paese(1, "Italia")
        );
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(wrongList);
        when(paeseRepository.findAll(Sort.by(Sort.Direction.DESC, "nome"))).thenReturn(oracleList);

        List<Paese> paeseList = gestionePaese.getPaesiSortedByName(Sort.Direction.DESC);
        assertEquals(oracleList, paeseList);
    }

    @Test
    void getPaesiSortedByNameAsc() {
        List<Paese> wrongList = List.of(
                new Paese(1, "Francia")
        );
        List<Paese> oracleList = List.of(
                new Paese(1, "Italia")
        );
        when(paeseRepository.findAll(any(Sort.class))).thenReturn(wrongList);
        when(paeseRepository.findAll(Sort.by(Sort.Direction.ASC, "nome"))).thenReturn(oracleList);

        List<Paese> paeseList = gestionePaese.getPaesiSortedByName(Sort.Direction.ASC);
        assertEquals(oracleList, paeseList);
    }

}
