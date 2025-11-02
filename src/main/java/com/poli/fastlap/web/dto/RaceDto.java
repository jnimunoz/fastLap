package com.poli.fastlap.web.dto;

import com.poli.fastlap.domain.race.Race;
import com.poli.fastlap.domain.race.RaceStatus;

import java.time.LocalDateTime;

public record RaceDto(
        Long id,
        String name,
        String description,
        RaceStatus status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        int totalLaps
) {
    public static RaceDto fromEntity(Race race) {
        int lapCount = race.getLaps() == null ? 0 : race.getLaps().size();
        return new RaceDto(
                race.getId(),
                race.getName(),
                race.getDescription(),
                race.getStatus(),
                race.getStartTime(),
                race.getEndTime(),
                lapCount
        );
    }
}
