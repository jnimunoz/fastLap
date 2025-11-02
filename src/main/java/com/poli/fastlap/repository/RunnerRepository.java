package com.poli.fastlap.repository;

import com.poli.fastlap.domain.runner.Runner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RunnerRepository extends JpaRepository<Runner, Long> {

    Optional<Runner> findByNicknameIgnoreCase(String nickname);

    boolean existsByNicknameIgnoreCase(String nickname);
}
