package com.poli.fastlap.web.api;

import com.poli.fastlap.service.race.LapRecordRequest;
import com.poli.fastlap.service.race.RaceAlreadyInProgressException;
import com.poli.fastlap.service.race.RaceNotFoundException;
import com.poli.fastlap.service.race.RaceNotInProgressException;
import com.poli.fastlap.service.race.RaceService;
import com.poli.fastlap.service.race.StartRaceRequest;
import com.poli.fastlap.web.dto.LapDto;
import com.poli.fastlap.web.dto.RaceDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/races")
@RequiredArgsConstructor
public class RaceRestController {

    private final RaceService raceService;

    @PostMapping
    public ResponseEntity<RaceDto> startRace(@Valid @RequestBody StartRaceRequest request) {
        RaceDto race = raceService.startRace(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(race);
    }

    @PostMapping("/{raceId}/finish")
    public RaceDto finishRace(@PathVariable Long raceId) {
        return raceService.finishRace(raceId);
    }

    @GetMapping("/active")
    public ResponseEntity<RaceDto> getActiveRace() {
        Optional<RaceDto> race = raceService.getActiveRace();
        return race.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/{raceId}")
    public RaceDto findById(@PathVariable Long raceId) {
        return raceService.findById(raceId);
    }

    @PostMapping("/{raceId}/laps")
    public ResponseEntity<LapDto> recordLap(@PathVariable Long raceId,
                                            @Valid @RequestBody LapRecordRequest request) {
        LapDto lap = raceService.recordLap(raceId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(lap);
    }

    @ExceptionHandler(RaceAlreadyInProgressException.class)
    public ResponseEntity<String> handleRaceInProgress(RaceAlreadyInProgressException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(RaceNotFoundException.class)
    public ResponseEntity<String> handleRaceNotFound(RaceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(RaceNotInProgressException.class)
    public ResponseEntity<String> handleRaceNotInProgress(RaceNotInProgressException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Solicitud inválida")
                .orElse("Solicitud inválida");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
}
