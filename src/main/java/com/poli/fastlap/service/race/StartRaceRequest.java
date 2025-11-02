package com.poli.fastlap.service.race;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StartRaceRequest(
        @NotBlank(message = "El nombre de la carrera es obligatorio")
        @Size(max = 80)
        String name,

        @Size(max = 255)
        String description
) {
}
