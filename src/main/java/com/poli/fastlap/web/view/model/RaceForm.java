package com.poli.fastlap.web.view.model;

import com.poli.fastlap.service.race.StartRaceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RaceForm {

    @NotBlank(message = "El nombre de la carrera es obligatorio")
    @Size(max = 80)
    private String name;

    @Size(max = 255)
    private String description;

    public StartRaceRequest toRequest() {
        return new StartRaceRequest(name, description);
    }
}
