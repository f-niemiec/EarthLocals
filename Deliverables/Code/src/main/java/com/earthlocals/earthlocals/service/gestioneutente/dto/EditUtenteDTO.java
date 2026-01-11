package com.earthlocals.earthlocals.service.gestioneutente.dto;

import com.earthlocals.earthlocals.model.Utente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditUtenteDTO {

    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;
    @NotBlank(message = "Il cognome è obbligatorio")
    private String cognome;
    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "L'email inserita non è valida")
    private String email;
    @NotNull
    private Integer nazionalita;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "La data di nascita inserita non è valida")
    private LocalDate dataNascita;
    @NotNull
    private Character sesso;


    public EditUtenteDTO(Utente utente) {
        this.nome = utente.getNome();
        this.cognome = utente.getCognome();
        this.email = utente.getEmail();
        this.nazionalita = utente.getNazionalita().getId();
        this.dataNascita = utente.getDataNascita();
        this.sesso = utente.getSesso();
    }


}
