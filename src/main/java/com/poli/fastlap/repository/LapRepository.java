package com.poli.fastlap.repository;

import com.poli.fastlap.domain.lap.Lap;
import com.poli.fastlap.domain.race.Race;
import com.poli.fastlap.domain.runner.Runner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LapRepository extends JpaRepository<Lap, Long> {

    Optional<Lap> findTopByRaceAndRunnerOrderByLapNumberDesc(Race race, Runner runner);

    List<Lap> findByRaceOrderByRecordedAtAsc(Race race);

    List<Lap> findByRunnerOrderByRecordedAtAsc(Runner runner);

    long countByRaceAndRunner(Race race, Runner runner);
}
