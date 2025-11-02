package com.poli.fastlap.web.dto;

import com.poli.fastlap.domain.runner.Runner;

import java.time.LocalDateTime;

public record RunnerDto(
        Long id,
        String firstName,
        String lastName,
        String nickname,
        String email,
        LocalDateTime createdAt
) {
    public static RunnerDto fromEntity(Runner runner) {
        return new RunnerDto(
                runner.getId(),
                runner.getFirstName(),
                runner.getLastName(),
                runner.getNickname(),
                runner.getEmail(),
                runner.getCreatedAt()
        );
    }
}
