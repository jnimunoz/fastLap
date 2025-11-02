package com.poli.fastlap.web.api;

import com.poli.fastlap.service.runner.RunnerAlreadyExistsException;
import com.poli.fastlap.service.runner.RunnerNotFoundException;
import com.poli.fastlap.service.runner.RunnerRegistrationRequest;
import com.poli.fastlap.service.runner.RunnerService;
import com.poli.fastlap.web.dto.RunnerDto;
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

import java.util.List;

@RestController
@RequestMapping("/api/runners")
@RequiredArgsConstructor
public class RunnerRestController {

    private final RunnerService runnerService;

    @PostMapping
    public ResponseEntity<RunnerDto> register(@Valid @RequestBody RunnerRegistrationRequest request) {
        RunnerDto created = runnerService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public List<RunnerDto> findAll() {
        return runnerService.findAll();
    }

    @GetMapping("/{id}")
    public RunnerDto findById(@PathVariable Long id) {
        return runnerService.findById(id);
    }

    @ExceptionHandler(RunnerAlreadyExistsException.class)
    public ResponseEntity<String> handleRunnerAlreadyExists(RunnerAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(RunnerNotFoundException.class)
    public ResponseEntity<String> handleRunnerNotFound(RunnerNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
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
