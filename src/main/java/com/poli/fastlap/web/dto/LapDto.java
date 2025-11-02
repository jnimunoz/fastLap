package com.poli.fastlap.web.dto;

import com.poli.fastlap.domain.lap.Lap;

import java.time.Duration;
import java.time.LocalDateTime;

public record LapDto(
        Long id,
        Long raceId,
    String raceName,
        Long runnerId,
        String runnerNickname,
        Integer lapNumber,
        Long lapTimeMillis,
        LocalDateTime recordedAt,
        String formattedLapTime
) {
    public static LapDto fromEntity(Lap lap) {
        Duration duration = lap.getLapDuration();
        String formatted = formatDuration(duration);
        return new LapDto(
                lap.getId(),
                lap.getRace().getId(),
                lap.getRace().getName(),
                lap.getRunner().getId(),
                lap.getRunner().getNickname(),
                lap.getLapNumber(),
                lap.getLapTimeMillis(),
                lap.getRecordedAt(),
                formatted
        );
    }

    private static String formatDuration(Duration duration) {
        return formatMillis(duration.toMillis());
    }

    public static String formatMillis(long millis) {
        long minutes = millis / 60000;
        long seconds = (millis % 60000) / 1000;
        long remainingMillis = millis % 1000;
        if (minutes > 0) {
            return String.format("%d:%02d.%03d", minutes, seconds, remainingMillis);
        }
        return String.format("%d.%03d", seconds, remainingMillis);
    }
}
