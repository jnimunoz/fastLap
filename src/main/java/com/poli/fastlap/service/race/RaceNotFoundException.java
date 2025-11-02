package com.poli.fastlap.service.race;

public class RaceNotFoundException extends RuntimeException {

    public RaceNotFoundException(Long raceId) {
        super("No se encontró la carrera con id %d".formatted(raceId));
    }

    public RaceNotFoundException() {
        super("No hay una carrera activa en este momento");
    }
}
