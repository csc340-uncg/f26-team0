package com.csc340.fitmatch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SessionNotesRequest(@NotBlank @Size(max = 2000) String notes) {
}
