package com.csc340.fitmatch.dto;

import com.csc340.fitmatch.entity.Timeslot;

import java.time.LocalDateTime;

public record TimeslotResponse(
        Long id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Boolean isAvailable,
        Long trainerId) {

    public static TimeslotResponse from(Timeslot timeslot) {
        return new TimeslotResponse(timeslot.getId(), timeslot.getStartTime(), timeslot.getEndTime(),
                timeslot.getIsAvailable(), timeslot.getTrainer().getId());
    }
}
