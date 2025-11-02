package com.poli.fastlap.web.dto;

import java.util.List;

public record RunnerLapHistoryDto(
        RunnerDto runner,
        int totalLaps,
        Long bestLapMillis,
        String formattedBestLap,
        Double averageLapMillis,
        String formattedAverageLap,
        List<LapDto> laps
) {
}
