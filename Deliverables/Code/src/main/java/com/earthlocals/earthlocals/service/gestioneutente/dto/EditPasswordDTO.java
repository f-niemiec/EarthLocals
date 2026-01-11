package com.earthlocals.earthlocals.service.gestioneutente.dto;

import com.earthlocals.earthlocals.utility.constraints.PasswordMatches;
import com.earthlocals.earthlocals.utility.interfaces.PasswordMatchingVerifiable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches(
        message = "Le password non coincidono",
        connectedField = "matchingPassword"
)
public class EditPasswordDTO implements PasswordMatchingVerifiable {

    @NotNull
    private String currentPassword;

    // TODO: Centralizzare il controllo delle password
    @NotBlank(message = "La nuova password è obbligatoria")
    @Length(
            min = 8,
            message = "La nuova password deve essere lunga almeno 8 caratteri"
    )
    @Pattern.List({
            @Pattern(
                    regexp = "^.*[a-z].*$",
                    message = "La nuova password deve contenere almeno un carattere minuscolo"
            ),
            @Pattern(
                    regexp = "^.*[A-Z].*$",
                    message = "La nuova password deve contenere almeno un carattere maiuscolo"
            ),
            @Pattern(
                    regexp = "^.*\\d.*$",
                    message = "La nuova password deve contenere almeno un numero"
            ),
            @Pattern(
                    // TODO: Magari rendiamo controllo per qualsiasi carattere non alfanumerico
                    regexp = "^.*[@$!%*?&].*$",
                    message = "La nuova password deve contenere almeno un carattere speciale"
            ),
            @Pattern(
                    regexp = "^[A-Za-z\\d@$!%*?&]*$",
                    message = "La nuova password contiene caratteri non validi"
            )
    })
    private String newPassword;
    private String matchingPassword;

    @Override
    public boolean isPasswordMatching() {
        return Objects.equals(newPassword, matchingPassword);
    }
}

