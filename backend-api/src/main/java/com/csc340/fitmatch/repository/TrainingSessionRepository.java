package com.csc340.fitmatch.repository;

import com.csc340.fitmatch.entity.TrainingSession;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
    List<TrainingSession> findAllByCustomerIdOrderByIdDesc(Long customerId);

    List<TrainingSession> findAllByTrainingService_Trainer_IdOrderByIdDesc(Long trainerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select trainingSession from TrainingSession trainingSession "
            + "where trainingSession.id = :id")
    Optional<TrainingSession> findByIdForUpdate(@Param("id") Long id);
}
