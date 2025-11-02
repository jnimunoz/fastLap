package com.poli.fastlap.web.view.model;

import com.poli.fastlap.service.runner.RunnerRegistrationRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RunnerForm {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60)
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 60)
    private String lastName;

    @NotBlank(message = "El apodo es obligatorio")
    @Size(max = 60)
    private String nickname;

    @Email(message = "El correo electrónico no es válido")
    @Size(max = 120)
    private String email;

    public RunnerRegistrationRequest toRequest() {
        return new RunnerRegistrationRequest(firstName, lastName, nickname, email);
    }
}
