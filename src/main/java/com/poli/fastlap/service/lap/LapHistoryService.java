package com.poli.fastlap.service.lap;

import com.poli.fastlap.domain.lap.Lap;
import com.poli.fastlap.domain.race.Race;
import com.poli.fastlap.domain.runner.Runner;
import com.poli.fastlap.repository.LapRepository;
import com.poli.fastlap.repository.RaceRepository;
import com.poli.fastlap.repository.RunnerRepository;
import com.poli.fastlap.service.race.RaceNotFoundException;
import com.poli.fastlap.service.runner.RunnerNotFoundException;
import com.poli.fastlap.web.dto.LapDto;
import com.poli.fastlap.web.dto.RaceDto;
import com.poli.fastlap.web.dto.RaceLapHistoryDto;
import com.poli.fastlap.web.dto.RunnerDto;
import com.poli.fastlap.web.dto.RunnerLapHistoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.OptionalDouble;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LapHistoryService {

    private final LapRepository lapRepository;
    private final RunnerRepository runnerRepository;
    private final RaceRepository raceRepository;

    public RunnerLapHistoryDto getHistoryForRunner(Long runnerId) {
        Runner runner = runnerRepository.findById(runnerId)
                .orElseThrow(() -> new RunnerNotFoundException(runnerId));

        List<LapDto> laps = lapRepository.findByRunnerOrderByRecordedAtAsc(runner).stream()
                .map(LapDto::fromEntity)
                .toList();

        int totalLaps = laps.size();
        Long bestLapMillis = laps.stream()
                .map(LapDto::lapTimeMillis)
                .min(Long::compareTo)
                .orElse(null);
        OptionalDouble averageOpt = laps.stream()
                .mapToLong(LapDto::lapTimeMillis)
                .average();
        Double averageMillis = averageOpt.isPresent() ? averageOpt.getAsDouble() : null;

        String formattedBest = bestLapMillis != null ? LapDto.formatMillis(bestLapMillis) : null;
        String formattedAverage = averageMillis != null ? LapDto.formatMillis(Math.round(averageMillis)) : null;

        return new RunnerLapHistoryDto(
                RunnerDto.fromEntity(runner),
                totalLaps,
                bestLapMillis,
                formattedBest,
                averageMillis,
                formattedAverage,
                laps
        );
    }

    public RaceLapHistoryDto getHistoryForRace(Long raceId) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new RaceNotFoundException(raceId));
        List<LapDto> laps = lapRepository.findByRaceOrderByRecordedAtAsc(race).stream()
                .map(LapDto::fromEntity)
                .toList();
        return new RaceLapHistoryDto(RaceDto.fromEntity(race), laps);
    }
}
