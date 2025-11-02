package com.poli.fastlap.web.api;

import com.poli.fastlap.service.lap.LapHistoryService;
import com.poli.fastlap.service.race.RaceNotFoundException;
import com.poli.fastlap.service.runner.RunnerNotFoundException;
import com.poli.fastlap.web.dto.RaceLapHistoryDto;
import com.poli.fastlap.web.dto.RunnerLapHistoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryRestController {

    private final LapHistoryService lapHistoryService;

    @GetMapping("/runners/{runnerId}")
    public RunnerLapHistoryDto getRunnerHistory(@PathVariable Long runnerId) {
        return lapHistoryService.getHistoryForRunner(runnerId);
    }

    @GetMapping("/races/{raceId}")
    public RaceLapHistoryDto getRaceHistory(@PathVariable Long raceId) {
        return lapHistoryService.getHistoryForRace(raceId);
    }

    @ExceptionHandler(RunnerNotFoundException.class)
    public ResponseEntity<String> handleRunnerNotFound(RunnerNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(RaceNotFoundException.class)
    public ResponseEntity<String> handleRaceNotFound(RaceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
