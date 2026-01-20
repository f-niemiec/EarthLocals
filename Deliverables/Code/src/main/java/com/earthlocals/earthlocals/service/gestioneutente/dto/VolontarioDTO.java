package com.earthlocals.earthlocals.service.gestioneutente.dto;


import com.earthlocals.earthlocals.utility.constraints.DateOverlap;
import com.earthlocals.earthlocals.utility.constraints.FileType;
import com.earthlocals.earthlocals.utility.interfaces.DateOverlapVerifier;
import jakarta.validation.constraints.*;
import jakarta.validation.groups.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@DateOverlap(connectedField = "dataScadenzaPassaporto", message = "La data di scadenza deve essere successiva alla data di emissione")
public class VolontarioDTO extends UtenteDTO implements DateOverlapVerifier {

    @NotBlank(message = "Il numero del passaporto è obbligatorio")
    @Pattern.List({
            @Pattern(
                    regexp = "^.{1,9}$",
                    message = "Il numero del passaporto deve contenere al massimo 9 caratteri"
            ),
            @Pattern(
                    regexp = "^[A-Z0-9]*$",
                    message = "Il numero del passaporto deve contenere solo caratteri alfanumerici maiuscoli"
            )
    })
    private String numeroPassaporto;
    @NotNull(message = "La data di scadenza del passaporto è obbligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "La data di scadenza del passaporto inserita non è valida")
    private LocalDate dataScadenzaPassaporto;
    @NotNull(message = "La data di emissione del passaporto è obbligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "La data di emissione del passaporto inserita non è valida")
    private LocalDate dataEmissionePassaporto;
    @NotNull
    @FileType(allowedExtensions = {"application/pdf"}, groups = {Default.class, PassaportoGroup.class})
    private MultipartFile passaporto;

    public VolontarioDTO(String nome, String cognome, String email, String password, String matchingPassword, Integer nazionalita, LocalDate dataNascita, Character sesso, String numeroPassaporto, LocalDate dataScadenzaPassaporto, LocalDate dataEmissionePassaporto, MultipartFile passaporto) {
        super(nome, cognome, email, password, matchingPassword, nazionalita, dataNascita, sesso);
        this.numeroPassaporto = numeroPassaporto;
        this.dataScadenzaPassaporto = dataScadenzaPassaporto;
        this.dataEmissionePassaporto = dataEmissionePassaporto;
        this.passaporto = passaporto;
    }

    @Override
    public boolean isDateOverlapping() {
        if (dataEmissionePassaporto == null || dataScadenzaPassaporto == null) {
            return false;
        }
        return dataScadenzaPassaporto.isBefore(dataEmissionePassaporto);
    }

    public interface PassaportoGroup {
    }
}
