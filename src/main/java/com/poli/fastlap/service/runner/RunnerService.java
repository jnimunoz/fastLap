package com.poli.fastlap.service.runner;

import com.poli.fastlap.domain.runner.Runner;
import com.poli.fastlap.repository.RunnerRepository;
import com.poli.fastlap.web.dto.RunnerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RunnerService {

    private final RunnerRepository runnerRepository;

    public RunnerDto register(RunnerRegistrationRequest request) {
        if (runnerRepository.existsByNicknameIgnoreCase(request.nickname())) {
            throw new RunnerAlreadyExistsException(request.nickname());
        }
        Runner runner = Runner.builder()
                .firstName(request.firstName().trim())
                .lastName(request.lastName().trim())
                .nickname(request.nickname().trim())
                .email(request.email() == null ? null : request.email().trim())
                .build();
        Runner saved = runnerRepository.save(runner);
        return RunnerDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<RunnerDto> findAll() {
        return runnerRepository.findAll().stream()
                .map(RunnerDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RunnerDto findById(Long id) {
        return runnerRepository.findById(id)
                .map(RunnerDto::fromEntity)
                .orElseThrow(() -> new RunnerNotFoundException(id));
    }
}
