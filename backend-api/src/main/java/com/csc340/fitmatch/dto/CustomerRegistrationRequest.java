package com.csc340.fitmatch.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CustomerRegistrationRequest(
                @NotBlank @Size(max = 100) String name,
                @NotBlank @Email @Size(max = 254) String email,
                @NotBlank @Size(min = 8, max = 72) String password,
                @Size(max = 20) String phoneNumber,
                @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal currentWeight,
                @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal goalWeight,
                @Size(max = 50) String fitnessLevel,
                @Size(max = 2000) String fitnessGoals,
                @Size(max = 2000) String injuriesOrHealthConcerns) {
}
