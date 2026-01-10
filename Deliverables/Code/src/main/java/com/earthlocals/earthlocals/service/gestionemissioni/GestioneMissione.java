package com.earthlocals.earthlocals.service.gestionemissioni;

import com.earthlocals.earthlocals.model.Paese;
import com.earthlocals.earthlocals.model.PaeseRepository;
import com.earthlocals.earthlocals.service.gestionemissioni.dto.MissioneDTO;
import com.earthlocals.earthlocals.service.gestionemissioni.exception.MissioneNotAcceptableException;
import com.earthlocals.earthlocals.service.gestionemissioni.pictures.PicturesStorageService;
import com.earthlocals.earthlocals.model.Missione;
import com.earthlocals.earthlocals.model.MissioneRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class GestioneMissione {
    @Autowired
    private MissioneRepository missioneRepository;

    @Autowired
    private PaeseRepository paeseRepository;

    @Autowired
    private PicturesStorageService storageService;

    public Missione registerMissione(@Valid MissioneDTO missioneDTO) throws Exception{
        var missioneBuilder = Missione.missioneBuilder();
        String fileName = storageService.acceptUpload(missioneDTO.getFoto());
        //Forse non la miglior gestione di paese
        Paese paese = paeseRepository.findById(missioneDTO.getPaese()).orElseThrow();
        missioneBuilder.dataFine(missioneDTO.getDataFine());
        missioneBuilder.creatore(missioneDTO.getCreatore());
        missioneBuilder.citta(missioneDTO.getCitta());
        missioneBuilder.competenzeRichieste(missioneDTO.getCompetenzeRichieste());
        missioneBuilder.dataInizio(missioneDTO.getDataInizio());
        missioneBuilder.descrizione(missioneDTO.getDescrizione());
        missioneBuilder.immagine(fileName);
        missioneBuilder.nome(missioneDTO.getNome());
        missioneBuilder.paese(paese);
        missioneBuilder.stato(Missione.MissioneStato.PENDING);
        if(missioneDTO.getRequisitiExtra() != null ){
            missioneBuilder.requisitiExtra(missioneDTO.getRequisitiExtra());
        }

        var missione = missioneBuilder.build();

        missioneRepository.save(missione);
        return missione;
    }

    public boolean acceptMissione(Long id) {
        var missione = missioneRepository.findById(id).orElseThrow();
        if (!missione.getStato().equals(Missione.MissioneStato.PENDING)) {
            throw new MissioneNotAcceptableException();
        }
        missione.accettaMissione();
        return true;
    }

    public boolean rejectMissione(Long id) {
        var missione = missioneRepository.findById(id).orElseThrow();
        if (!missione.getStato().equals(Missione.MissioneStato.PENDING)) {
            //Forse opportuno definirne un'altra
            throw new MissioneNotAcceptableException();
        }
        missione.rifiutaMissione();
        return true;
    }

}