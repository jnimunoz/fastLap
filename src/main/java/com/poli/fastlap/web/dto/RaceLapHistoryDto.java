package com.poli.fastlap.web.dto;

import java.util.List;

public record RaceLapHistoryDto(
        RaceDto race,
        List<LapDto> laps
) {
}
