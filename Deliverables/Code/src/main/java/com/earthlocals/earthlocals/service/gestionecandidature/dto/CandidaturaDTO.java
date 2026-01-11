package com.earthlocals.earthlocals.service.gestionecandidature.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidaturaDTO {

    @NotEmpty(message = "Il candidato è obbligatorio")
    private Long candidatoId;

    @NotEmpty(message = "La missione è obbligatoria")
    private Long missioneId;
}
