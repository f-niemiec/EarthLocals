package com.earthlocals.earthlocals.service.gestionemissioni.dto;

import com.earthlocals.earthlocals.model.Utente;
import com.earthlocals.earthlocals.utility.interfaces.DateOverlapVerifier;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MissioneDTO implements DateOverlapVerifier {

    @NotBlank(message = "Il nome della missione è obbligatorio")
    @Size(min = 5, max = 100, message = "Il nome deve essere tra 5 e 100 caratteri")
    private String nome;

    @NotNull(message = "Il paese è obbligatorio")
    private Integer paese;

    @NotBlank(message = "La città è obbligatoria")
    private String citta;

    @NotBlank(message = "La descrizione è obbligatoria")
    @Size(min = 20, message = "La descrizione deve essere di almeno 20 caratteri")
    private String descrizione;

    @NotNull(message = "La data di inizio è obbligatoria")
    @FutureOrPresent(message = "La missione non può iniziare nel passato")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataInizio;

    @NotNull(message = "La data di fine è obbligatoria")
    @Future(message = "La data di fine deve essere nel futuro")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataFine;

    @NotBlank(message = "Le competenze richieste sono obbligatorie")
    private String competenzeRichieste;

    private String requisitiExtra;

    @NotNull
    private MultipartFile foto;

    private Utente creatore;

    @Override
    public boolean isDateOverlapping() {
        return dataFine.isBefore(dataInizio);
    }

}

