package com.csc340.fitmatch.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TrainerUpdateRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email @Size(max = 254) String email,
        @Size(max = 2000) String biography,
        @Size(max = 2000) String certifications,
        @NotNull @Min(0) Integer yearsOfExperience,
        @Size(max = 1000) String specialties) {
}
