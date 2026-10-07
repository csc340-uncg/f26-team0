package com.csc340.fitmatch.repository;

import com.csc340.fitmatch.entity.TrainingService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TrainingServiceRepository extends JpaRepository<TrainingService, Long> {
        List<TrainingService> findAllByTrainerIdOrderById(Long trainerId);

        List<TrainingService> findAllByTrainerIdAndStatusIgnoreCaseOrderById(
                        Long trainerId, String status);

        List<TrainingService> findAllByCategoryIgnoreCaseAndStatusIgnoreCaseOrderById(
                        String category, String status);

        List<TrainingService> findAllByStatusIgnoreCaseOrderById(String string);
}
