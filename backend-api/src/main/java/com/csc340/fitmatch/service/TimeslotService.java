package com.csc340.fitmatch.service;

import com.csc340.fitmatch.dto.TimeslotRequest;
import com.csc340.fitmatch.dto.TimeslotResponse;
import com.csc340.fitmatch.entity.Timeslot;
import com.csc340.fitmatch.entity.Trainer;
import com.csc340.fitmatch.repository.TimeslotRepository;
import com.csc340.fitmatch.repository.TrainerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TimeslotService {
    private final TimeslotRepository timeslotRepository;
    private final TrainerRepository trainerRepository;

    public TimeslotService(TimeslotRepository timeslotRepository, TrainerRepository trainerRepository) {
        this.timeslotRepository = timeslotRepository;
        this.trainerRepository = trainerRepository;
    }

    public TimeslotResponse create(Long trainerId, TimeslotRequest request) {
        Trainer trainer = findTrainer(trainerId);
        validateRange(request.startTime(), request.endTime());
        Timeslot timeslot = new Timeslot();
        timeslot.setTrainer(trainer);
        apply(timeslot, request);
        return TimeslotResponse.from(timeslotRepository.save(timeslot));
    }

    @Transactional(readOnly = true)
    public List<TimeslotResponse> list(Long trainerId, boolean availableOnly) {
        if (!trainerRepository.existsById(trainerId)) {
            throw new NotFoundException("Trainer " + trainerId + " was not found.");
        }
        List<Timeslot> timeslots = availableOnly
                ? timeslotRepository.findAllByTrainerIdAndIsAvailableTrueOrderByStartTime(trainerId)
                : timeslotRepository.findAllByTrainerIdOrderByStartTime(trainerId);
        return timeslots.stream().map(TimeslotResponse::from).toList();
    }

    public TimeslotResponse update(Long trainerId, Long timeslotId, TimeslotRequest request) {
        Timeslot timeslot = lockTimeslot(timeslotId);
        if (!timeslot.getTrainer().getId().equals(trainerId)) {
            throw new NotFoundException("Timeslot " + timeslotId + " was not found for trainer "
                    + trainerId + ".");
        }
        if (timeslot.getTrainingSession() != null) {
            throw new ConflictException("A booked timeslot cannot be edited.");
        }
        validateRange(request.startTime(), request.endTime());
        apply(timeslot, request);
        return TimeslotResponse.from(timeslotRepository.save(timeslot));
    }

    public void delete(Long trainerId, Long timeslotId) {
        Timeslot timeslot = lockTimeslot(timeslotId);
        if (!timeslot.getTrainer().getId().equals(trainerId)) {
            throw new NotFoundException("Timeslot " + timeslotId + " was not found for trainer "
                    + trainerId + ".");
        }
        if (timeslot.getTrainingSession() != null) {
            throw new ConflictException("A booked timeslot cannot be deleted.");
        }
        timeslotRepository.delete(timeslot);
    }

    private void apply(Timeslot timeslot, TimeslotRequest request) {
        timeslot.setStartTime(request.startTime());
        timeslot.setEndTime(request.endTime());
        timeslot.setIsAvailable(request.isAvailable());
    }

    private void validateRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new BadRequestException("endTime must be after startTime.");
        }
        if (!startTime.isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Timeslots must start in the future.");
        }
    }

    private Timeslot lockTimeslot(Long timeslotId) {
        return timeslotRepository.findByIdForUpdate(timeslotId)
                .orElseThrow(() -> new NotFoundException("Timeslot " + timeslotId + " was not found."));
    }

    private Trainer findTrainer(Long trainerId) {
        return trainerRepository.findById(trainerId)
                .orElseThrow(() -> new NotFoundException("Trainer " + trainerId + " was not found."));
    }
}
