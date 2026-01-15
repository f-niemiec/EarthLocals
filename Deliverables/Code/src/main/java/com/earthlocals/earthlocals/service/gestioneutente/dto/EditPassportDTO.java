package com.earthlocals.earthlocals.service.gestioneutente.dto;

import com.earthlocals.earthlocals.model.Volontario;
import com.earthlocals.earthlocals.utility.constraints.FileType;
import jakarta.validation.constraints.*;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditPassportDTO {

    @NotBlank(message = "Il numero del passaporto è obbligatorio")
    @Pattern(
            regexp = "^[A-Z0-9]{1,9}$",
            message = "Il numero del passaporto deve contenere al massimo 9 cifre "
    )
    private String numeroPassaporto;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "La data di scadenza del passaporto inserita non è valida")
    private LocalDate dataScadenzaPassaporto;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "La data di emissione del passaporto inserita non è valida")
    private LocalDate dataEmissionePassaporto;
    @NotNull
    @FileType(allowedExtensions = {"application/pdf"}, groups = {Default.class, EditPassportDTO.VolontarioPassport.class})
    private MultipartFile passaporto;

    public EditPassportDTO(Volontario volontario) {
        this.numeroPassaporto = volontario.getNumeroPassaporto();
        this.dataScadenzaPassaporto = volontario.getDataScadenzaPassaporto();
        this.dataEmissionePassaporto = volontario.getDataEmissionePassaporto();
    }

    public interface VolontarioPassport {

    }
}
