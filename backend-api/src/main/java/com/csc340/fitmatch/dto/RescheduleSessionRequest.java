package com.csc340.fitmatch.dto;

import jakarta.validation.constraints.NotNull;

public record RescheduleSessionRequest(@NotNull Long timeslotId) {
}
