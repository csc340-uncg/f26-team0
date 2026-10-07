package com.csc340.fitmatch.dto;

import com.csc340.fitmatch.entity.Trainer;

public record TrainerResponse(
        Long id,
        String name,
        String email,
        String accountStatus,
        String biography,
        String certifications,
        Integer yearsOfExperience,
        String specialties) {

    public static TrainerResponse from(Trainer trainer) {
        return new TrainerResponse(trainer.getId(), trainer.getName(), trainer.getEmail(),
                trainer.getAccountStatus(), trainer.getBiography(), trainer.getCertifications(),
                trainer.getYearsOfExperience(), trainer.getSpecialties());
    }
}
