package com.csc340.fitmatch.repository;

import com.csc340.fitmatch.entity.Timeslot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TimeslotRepository extends JpaRepository<Timeslot, Long> {
    List<Timeslot> findAllByTrainerIdOrderByStartTime(Long trainerId);

    List<Timeslot> findAllByTrainerIdAndIsAvailableTrueOrderByStartTime(Long trainerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select timeslot from Timeslot timeslot where timeslot.id = :id")
    Optional<Timeslot> findByIdForUpdate(@Param("id") Long id);
}
