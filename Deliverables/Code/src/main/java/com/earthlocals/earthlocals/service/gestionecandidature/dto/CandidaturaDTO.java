package com.earthlocals.earthlocals.service.gestionecandidature.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidaturaDTO {

    @NotNull(message = "Il candidato è obbligatorio")
    @PositiveOrZero
    private Long candidatoId;

    @NotNull(message = "La missione è obbligatoria")
    @PositiveOrZero
    private Long missioneId;
}
