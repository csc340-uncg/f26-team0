package com.csc340.fitmatch.dto;

import com.csc340.fitmatch.entity.Timeslot;
import com.csc340.fitmatch.entity.TrainingSession;

import java.time.LocalDateTime;

public record TrainingSessionResponse(
        Long id,
        String notes,
        String status,
        String level,
        String location,
        Long customerId,
        String customerName,
        Long trainingServiceId,
        String trainingServiceName,
        Long trainerId,
        Long timeslotId,
        LocalDateTime startTime,
        LocalDateTime endTime) {

    public static TrainingSessionResponse from(TrainingSession session) {
        Timeslot timeslot = session.getTimeslot();
        return new TrainingSessionResponse(session.getId(), session.getNotes(), session.getStatus(),
                session.getLevel(), session.getLocation(), session.getCustomer().getId(),
                session.getCustomer().getName(), session.getTrainingService().getId(),
                session.getTrainingService().getName(), session.getTrainingService().getTrainer().getId(),
                timeslot == null ? null : timeslot.getId(),
                timeslot == null ? null : timeslot.getStartTime(),
                timeslot == null ? null : timeslot.getEndTime());
    }
}
