package com.csc340.fitmatch.dto;

import com.csc340.fitmatch.entity.TrainingService;

import java.math.BigDecimal;

public record TrainingServiceResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String category,
        String status,
        Long trainerId) {

    public static TrainingServiceResponse from(TrainingService trainingService) {
        return new TrainingServiceResponse(trainingService.getId(), trainingService.getName(),
                trainingService.getDescription(), trainingService.getPrice(),
                trainingService.getCategory(), trainingService.getStatus(),
                trainingService.getTrainer().getId());
    }
}
