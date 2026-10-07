package com.csc340.fitmatch.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookSessionRequest(
        @NotNull Long trainingServiceId,
        @NotNull Long timeslotId,
        @Size(max = 2000) String notes,
        @Size(max = 50) String level,
        @Size(max = 255) String location) {
}
