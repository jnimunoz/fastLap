package com.poli.fastlap.service.race;

public class RaceAlreadyInProgressException extends RuntimeException {

    public RaceAlreadyInProgressException() {
        super("Ya existe una carrera en progreso. Debes finalizarla antes de iniciar otra.");
    }
}
