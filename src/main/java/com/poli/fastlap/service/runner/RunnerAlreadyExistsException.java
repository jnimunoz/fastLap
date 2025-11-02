package com.poli.fastlap.service.runner;

public class RunnerAlreadyExistsException extends RuntimeException {

    public RunnerAlreadyExistsException(String nickname) {
        super("El apodo '%s' ya está registrado".formatted(nickname));
    }
}
