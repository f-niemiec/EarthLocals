package com.earthlocals.earthlocals.gestioneutente.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtenteDTO {

    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    private String cognome;

    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "L'email inserita non è valida")
    private String email;

    @NotBlank(message = "La password è obbligatoria")
    @Length(
            min = 8,
            message = "La password deve essere lunga almeno 8 caratteri"
    )
    @Pattern.List({
            @Pattern(
                    regexp = "^.*[a-z].*$",
                    message = "La password deve contenere almeno un carattere minuscolo"
            ),
            @Pattern(
                    regexp = "^.*[A-Z].*$",
                    message = "La password deve contenere almeno un carattere maiuscolo"
            ),
            @Pattern(
                    regexp = "^.*\\d.*$",
                    message = "La password deve contenere almeno un numero"
            ),
            @Pattern(
                    // TODO: Magari rendiamo controllo per qualsiasi carattere non alfanumerico
                    regexp = "^.*[@$!%*?&].*$",
                    message = "La password deve contenere almeno un carattere speciale"
            ),
            @Pattern(
                    regexp = "^[A-Za-z\\d@$!%*?&]*$",
                    message = "La password contiene caratteri non validi"
            )
    })
    private String password;
    private String matchingPassword;

    @NotNull
    private Integer nazionalita;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "La data di nascita inserita non è valida")
    private Date dataNascita;


    @NotNull
    private Character sesso;

}
