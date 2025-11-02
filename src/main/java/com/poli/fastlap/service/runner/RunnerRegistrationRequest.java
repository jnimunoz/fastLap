package com.poli.fastlap.service.runner;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RunnerRegistrationRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 60)
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 60)
        String lastName,

        @NotBlank(message = "El apodo es obligatorio")
        @Size(max = 60)
        String nickname,

        @Email(message = "El correo electrónico no es válido")
        @Size(max = 120)
        String email
) {
}
