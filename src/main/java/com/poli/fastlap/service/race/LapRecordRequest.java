package com.poli.fastlap.service.race;

import jakarta.validation.constraints.NotNull;

public record LapRecordRequest(
        @NotNull(message = "El identificador del corredor es obligatorio")
        Long runnerId
) {
}
