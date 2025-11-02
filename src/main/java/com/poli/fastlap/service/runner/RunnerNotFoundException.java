package com.poli.fastlap.service.runner;

public class RunnerNotFoundException extends RuntimeException {

    public RunnerNotFoundException(Long id) {
        super("No se encontró un corredor con id %d".formatted(id));
    }
}
