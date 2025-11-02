package com.poli.fastlap.service.race;

import com.poli.fastlap.domain.race.RaceStatus;
import com.poli.fastlap.service.runner.RunnerRegistrationRequest;
import com.poli.fastlap.service.runner.RunnerService;
import com.poli.fastlap.web.dto.LapDto;
import com.poli.fastlap.web.dto.RaceDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RaceServiceTest {

    @Autowired
    private RaceService raceService;

    @Autowired
    private RunnerService runnerService;

    private Long runnerId;

    @BeforeEach
    void setupRunner() {
        runnerId = runnerService.register(new RunnerRegistrationRequest(
                "Mario",
                "Pérez",
                "mario",
                "mario@example.com"
        )).id();
    }

    @Test
    void startRaceShouldCreateActiveRace() {
        RaceDto race = raceService.startRace(new StartRaceRequest("Test Race", "Sesión de prueba"));
        assertThat(race.id()).isNotNull();
        assertThat(race.status()).isEqualTo(RaceStatus.IN_PROGRESS);
        assertThat(raceService.getActiveRace()).isPresent();
    }

    @Test
    void recordLapShouldIncreaseLapNumber() {
        RaceDto race = raceService.startRace(new StartRaceRequest("Track Day", null));
        LapDto lap1 = raceService.recordLap(race.id(), new LapRecordRequest(runnerId));
        LapDto lap2 = raceService.recordLap(race.id(), new LapRecordRequest(runnerId));

        assertThat(lap1.lapNumber()).isEqualTo(1);
        assertThat(lap2.lapNumber()).isEqualTo(2);
        assertThat(lap2.lapTimeMillis()).isNotNegative();
    }

    @Test
    void finishRaceShouldUpdateStatus() {
        RaceDto race = raceService.startRace(new StartRaceRequest("Evening Run", ""));
        RaceDto finished = raceService.finishRace(race.id());

        assertThat(finished.status()).isEqualTo(RaceStatus.COMPLETED);
        assertThat(raceService.getActiveRace()).isEmpty();
    }
}
