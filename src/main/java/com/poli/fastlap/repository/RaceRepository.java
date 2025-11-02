package com.poli.fastlap.repository;

import com.poli.fastlap.domain.race.Race;
import com.poli.fastlap.domain.race.RaceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RaceRepository extends JpaRepository<Race, Long> {

    Optional<Race> findFirstByStatusOrderByStartTimeDesc(RaceStatus status);

    boolean existsByStatus(RaceStatus status);
}
