package com.poli.fastlap.service.race;

public class RaceNotInProgressException extends RuntimeException {

    public RaceNotInProgressException(Long raceId) {
        super("La carrera con id %d no está en progreso".formatted(raceId));
    }
}
