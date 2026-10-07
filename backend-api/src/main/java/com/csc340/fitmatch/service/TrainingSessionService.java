package com.csc340.fitmatch.service;

import com.csc340.fitmatch.dto.BookSessionRequest;
import com.csc340.fitmatch.dto.RescheduleSessionRequest;
import com.csc340.fitmatch.dto.SessionNotesRequest;
import com.csc340.fitmatch.dto.TrainingSessionResponse;
import com.csc340.fitmatch.entity.Customer;
import com.csc340.fitmatch.entity.Timeslot;
import com.csc340.fitmatch.entity.TrainingService;
import com.csc340.fitmatch.entity.TrainingSession;
import com.csc340.fitmatch.repository.CustomerRepository;
import com.csc340.fitmatch.repository.TimeslotRepository;
import com.csc340.fitmatch.repository.TrainingServiceRepository;
import com.csc340.fitmatch.repository.TrainingSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class TrainingSessionService {
    private final TrainingSessionRepository sessionRepository;
    private final CustomerRepository customerRepository;
    private final TrainingServiceRepository serviceRepository;
    private final TimeslotRepository timeslotRepository;

    public TrainingSessionService(
            TrainingSessionRepository sessionRepository,
            CustomerRepository customerRepository,
            TrainingServiceRepository serviceRepository,
            TimeslotRepository timeslotRepository) {
        this.sessionRepository = sessionRepository;
        this.customerRepository = customerRepository;
        this.serviceRepository = serviceRepository;
        this.timeslotRepository = timeslotRepository;
    }

    public TrainingSessionResponse book(Long customerId, BookSessionRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer " + customerId + " was not found."));
        TrainingService trainingService = serviceRepository.findById(request.trainingServiceId())
                .orElseThrow(() -> new NotFoundException(
                        "Training service " + request.trainingServiceId() + " was not found."));
        if (!"PUBLISHED".equalsIgnoreCase(trainingService.getStatus())) {
            throw new ConflictException("Only published services can be booked.");
        }
        Timeslot timeslot = lockTimeslot(request.timeslotId());
        ensureBookable(timeslot);
        if (!timeslot.getTrainer().getId().equals(trainingService.getTrainer().getId())) {
            throw new BadRequestException("The selected timeslot does not belong to the service's trainer.");
        }

        TrainingSession session = new TrainingSession();
        session.setCustomer(customer);
        session.setTrainingService(trainingService);
        session.setTimeslot(timeslot);
        session.setNotes(request.notes());
        session.setLevel(request.level());
        session.setLocation(request.location());
        session.setStatus("BOOKED");
        timeslot.setIsAvailable(false);
        return TrainingSessionResponse.from(sessionRepository.save(session));
    }

    @Transactional(readOnly = true)
    public List<TrainingSessionResponse> listForCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new NotFoundException("Customer " + customerId + " was not found.");
        }
        return sessionRepository.findAllByCustomerIdOrderByIdDesc(customerId)
                .stream().map(TrainingSessionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<TrainingSessionResponse> listForTrainer(Long trainerId) {
        return sessionRepository.findAllByTrainingService_Trainer_IdOrderByIdDesc(trainerId)
                .stream().map(TrainingSessionResponse::from).toList();
    }

    public TrainingSessionResponse cancel(Long customerId, Long sessionId) {
        TrainingSession session = lockSession(sessionId);
        ensureCustomerOwns(session, customerId);
        ensureBooked(session);
        session.setStatus("CANCELLED");
        Timeslot timeslot = lockTimeslot(session.getTimeslot().getId());
        timeslot.setIsAvailable(true);
        return TrainingSessionResponse.from(sessionRepository.save(session));
    }

    public TrainingSessionResponse reschedule(
            Long customerId, Long sessionId, RescheduleSessionRequest request) {
        TrainingSession session = lockSession(sessionId);
        ensureCustomerOwns(session, customerId);
        ensureBooked(session);
        Timeslot newTimeslot = lockTimeslot(request.timeslotId());
        ensureBookable(newTimeslot);
        if (!newTimeslot.getTrainer().getId().equals(session.getTrainingService().getTrainer().getId())) {
            throw new BadRequestException("The new timeslot must belong to the session's trainer.");
        }
        Timeslot oldTimeslot = lockTimeslot(session.getTimeslot().getId());
        oldTimeslot.setIsAvailable(true);
        newTimeslot.setIsAvailable(false);
        session.setTimeslot(newTimeslot);
        return TrainingSessionResponse.from(sessionRepository.save(session));
    }

    public TrainingSessionResponse markCompleted(Long trainerId, Long sessionId) {
        TrainingSession session = lockSession(sessionId);
        if (!session.getTrainingService().getTrainer().getId().equals(trainerId)) {
            throw new NotFoundException("Training session " + sessionId + " was not found for trainer "
                    + trainerId + ".");
        }
        ensureBooked(session);
        session.setStatus("COMPLETED");
        return TrainingSessionResponse.from(sessionRepository.save(session));
    }

    public TrainingSessionResponse updateNotes(
            Long customerId, Long sessionId, SessionNotesRequest request) {
        TrainingSession session = lockSession(sessionId);
        ensureCustomerOwns(session, customerId);
        if ("CANCELLED".equalsIgnoreCase(session.getStatus())) {
            throw new ConflictException("Notes cannot be updated for a cancelled session.");
        }
        session.setNotes(request.notes().trim());
        return TrainingSessionResponse.from(sessionRepository.save(session));
    }

    private void ensureBookable(Timeslot timeslot) {
        if (!Boolean.TRUE.equals(timeslot.getIsAvailable())) {
            throw new ConflictException("The selected timeslot is no longer available.");
        }
        if (timeslot.getBookedTrainingSession() != null) {
            throw new ConflictException("The selected timeslot already has a booked session.");
        }
        if (!timeslot.getStartTime().isAfter(LocalDateTime.now())) {
            throw new ConflictException("The selected timeslot is in the past.");
        }
    }

    private void ensureBooked(TrainingSession session) {
        if (!"BOOKED".equals(session.getStatus().toUpperCase(Locale.ROOT))) {
            throw new ConflictException("Only booked sessions can be changed.");
        }
    }

    private void ensureCustomerOwns(TrainingSession session, Long customerId) {
        if (!session.getCustomer().getId().equals(customerId)) {
            throw new NotFoundException("Training session " + session.getId() + " was not found for customer "
                    + customerId + ".");
        }
    }

    private Timeslot lockTimeslot(Long timeslotId) {
        return timeslotRepository.findByIdForUpdate(timeslotId)
                .orElseThrow(() -> new NotFoundException("Timeslot " + timeslotId + " was not found."));
    }

    private TrainingSession lockSession(Long sessionId) {
        return sessionRepository.findByIdForUpdate(sessionId)
                .orElseThrow(() -> new NotFoundException(
                        "Training session " + sessionId + " was not found."));
    }
}
