package com.csc340.fitmatch.mvc;

import com.csc340.fitmatch.dto.TrainerResponse;
import com.csc340.fitmatch.dto.TrainingServiceResponse;

import java.util.List;

public record TrainerCard(
        TrainerResponse trainer,
        List<TrainingServiceResponse> services) {
}
