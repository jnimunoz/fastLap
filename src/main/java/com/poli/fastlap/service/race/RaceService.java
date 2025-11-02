package com.poli.fastlap.service.race;

import com.poli.fastlap.domain.lap.Lap;
import com.poli.fastlap.domain.race.Race;
import com.poli.fastlap.domain.race.RaceStatus;
import com.poli.fastlap.domain.runner.Runner;
import com.poli.fastlap.repository.LapRepository;
import com.poli.fastlap.repository.RaceRepository;
import com.poli.fastlap.repository.RunnerRepository;
import com.poli.fastlap.service.runner.RunnerNotFoundException;
import com.poli.fastlap.web.dto.LapDto;
import com.poli.fastlap.web.dto.RaceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RaceService {

    private final RaceRepository raceRepository;
    private final RunnerRepository runnerRepository;
    private final LapRepository lapRepository;

    public RaceDto startRace(StartRaceRequest request) {
        if (raceRepository.existsByStatus(RaceStatus.IN_PROGRESS)) {
            throw new RaceAlreadyInProgressException();
        }
        LocalDateTime now = LocalDateTime.now();
        Race race = Race.builder()
                .name(request.name().trim())
                .description(request.description() == null ? null : request.description().trim())
                .status(RaceStatus.IN_PROGRESS)
                .startTime(now)
                .build();
        Race saved = raceRepository.save(race);
        return RaceDto.fromEntity(saved);
    }

    public RaceDto finishRace(Long raceId) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new RaceNotFoundException(raceId));
        if (race.getStatus() != RaceStatus.IN_PROGRESS) {
            throw new RaceNotInProgressException(raceId);
        }
        race.setStatus(RaceStatus.COMPLETED);
        race.setEndTime(LocalDateTime.now());
        Race saved = raceRepository.save(race);
        return RaceDto.fromEntity(saved);
    }

    public LapDto recordLap(Long raceId, LapRecordRequest request) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new RaceNotFoundException(raceId));
        if (race.getStatus() != RaceStatus.IN_PROGRESS) {
            throw new RaceNotInProgressException(raceId);
        }
        Runner runner = runnerRepository.findById(request.runnerId())
                .orElseThrow(() -> new RunnerNotFoundException(request.runnerId()));

        LocalDateTime now = LocalDateTime.now();
        Optional<Lap> lastLapOptional = lapRepository.findTopByRaceAndRunnerOrderByLapNumberDesc(race, runner);
        int nextLapNumber = lastLapOptional.map(lap -> lap.getLapNumber() + 1).orElse(1);
        LocalDateTime referenceTime = lastLapOptional.map(Lap::getRecordedAt)
                .orElseGet(() -> {
                    LocalDateTime startTime = race.getStartTime();
                    return startTime != null ? startTime : now;
                });
        long lapTimeMillis = Math.max(0, Duration.between(referenceTime, now).toMillis());

        Lap lap = Lap.builder()
                .race(race)
                .runner(runner)
                .lapNumber(nextLapNumber)
                .lapTimeMillis(lapTimeMillis)
                .recordedAt(now)
                .build();
        Lap saved = lapRepository.save(lap);
        race.addLap(saved);
        return LapDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public Optional<RaceDto> getActiveRace() {
        return raceRepository.findFirstByStatusOrderByStartTimeDesc(RaceStatus.IN_PROGRESS)
                .map(RaceDto::fromEntity);
    }

    @Transactional(readOnly = true)
    public RaceDto findById(Long id) {
        Race race = raceRepository.findById(id)
                .orElseThrow(() -> new RaceNotFoundException(id));
        return RaceDto.fromEntity(race);
    }

    @Transactional(readOnly = true)
    public List<RaceDto> findAll() {
        return raceRepository.findAll(Sort.by(Sort.Direction.DESC, "startTime", "createdAt")).stream()
                .map(RaceDto::fromEntity)
                .collect(Collectors.toList());
    }
}
