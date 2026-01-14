package com.earthlocals.earthlocals.service.gestioneutente.dto;

import com.earthlocals.earthlocals.utility.constraints.PasswordMatches;
import com.earthlocals.earthlocals.utility.constraints.Sex;
import com.earthlocals.earthlocals.utility.interfaces.PasswordMatchingVerifiable;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches(
        message = "Le password non coincidono",
        connectedField = "matchingPassword",
        groups = {UtenteDTO.UtenteDTOPasswordsMatchGroup.class}
)
public class UtenteDTO implements PasswordMatchingVerifiable {

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
    @PositiveOrZero
    private Integer nazionalita;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "La data di nascita inserita non è valida")
    private LocalDate dataNascita;


    @NotNull
    @Sex
    private Character sesso;

    @Override
    public boolean isPasswordMatching() {
        return Objects.equals(password, matchingPassword);
    }

    public interface UtenteDTOPasswordsMatchGroup {
    }
}
