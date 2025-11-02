package com.poli.fastlap.service.runner;

import com.poli.fastlap.web.dto.RunnerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RunnerServiceTest {

    @Autowired
    private RunnerService runnerService;

    @Test
    void registerShouldPersistRunner() {
        RunnerRegistrationRequest request = new RunnerRegistrationRequest(
                "Ana",
                "García",
                "anita",
                "ana@example.com"
        );

        RunnerDto result = runnerService.register(request);

        assertThat(result.id()).isNotNull();
        assertThat(result.nickname()).isEqualTo("anita");
        assertThat(runnerService.findAll()).hasSize(1);
    }

    @Test
    void registerShouldRejectDuplicatedNickname() {
        RunnerRegistrationRequest request = new RunnerRegistrationRequest(
                "Luis",
                "Martínez",
                "luismi",
                "luis@example.com"
        );

        runnerService.register(request);

        assertThatThrownBy(() -> runnerService.register(request))
                .isInstanceOf(RunnerAlreadyExistsException.class)
                .hasMessageContaining("luismi");
    }
}
