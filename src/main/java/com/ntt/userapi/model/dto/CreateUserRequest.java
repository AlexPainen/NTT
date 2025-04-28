package com.ntt.userapi.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    // Using a basic @Email annotation first, we'll add custom regex validation in service
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format") // Basic format check
    private String email;

    @NotBlank(message = "Password cannot be blank")
    // Password format validation will be done in the service layer using the configured regex
    private String password;

    @NotNull(message = "Phones list cannot be null")
    @Valid // Validate elements within the list
    private List<PhoneRequest> phones;
}