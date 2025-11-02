package com.poli.fastlap.domain.lap;

import com.poli.fastlap.domain.race.Race;
import com.poli.fastlap.domain.runner.Runner;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "laps", indexes = {
        @Index(name = "idx_lap_race_runner", columnList = "race_id, runner_id"),
        @Index(name = "idx_lap_recorded_at", columnList = "recorded_at")
})
public class Lap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "race_id", nullable = false)
    private Race race;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "runner_id", nullable = false)
    private Runner runner;

    @Column(name = "lap_number", nullable = false)
    private Integer lapNumber;

    @Column(name = "lap_time_millis", nullable = false)
    private Long lapTimeMillis;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (recordedAt == null) {
            recordedAt = now;
        }
    }

    public Duration getLapDuration() {
        return Duration.ofMillis(lapTimeMillis);
    }
}
