package com.csc340.fitmatch.controller;

import com.csc340.fitmatch.entity.Customer;
import com.csc340.fitmatch.entity.Review;
import com.csc340.fitmatch.entity.Timeslot;
import com.csc340.fitmatch.entity.Trainer;
import com.csc340.fitmatch.entity.TrainingService;
import com.csc340.fitmatch.entity.TrainingSession;
import com.csc340.fitmatch.service.DirectEntityApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/direct")
public class DirectEntityApiController {
    private final DirectEntityApiService apiService;

    public DirectEntityApiController(DirectEntityApiService apiService) {
        this.apiService = apiService;
    }

    @PostMapping("/customers")
    public ResponseEntity<Customer> registerCustomer(@RequestBody Customer customer) {
        Customer created = apiService.registerCustomer(customer);
        return ResponseEntity.created(URI.create("/api/direct/customers/" + created.getId()))
                .body(created);
    }

    @GetMapping("/customers/{customerId}")
    public Customer getCustomer(@PathVariable Long customerId) {
        return apiService.getCustomer(customerId);
    }

    @PutMapping("/customers/{customerId}")
    public Customer updateCustomer(
            @PathVariable Long customerId, @RequestBody Customer customer) {
        return apiService.updateCustomer(customerId, customer);
    }

    @GetMapping("/customers/{customerId}/sessions")
    public List<TrainingSession> customerSessions(@PathVariable Long customerId) {
        return apiService.customerSessions(customerId);
    }

    @GetMapping("/customers/{customerId}/reviews")
    public List<Review> customerReviews(@PathVariable Long customerId) {
        return apiService.customerReviews(customerId);
    }

    @PostMapping("/customers/{customerId}/sessions")
    public ResponseEntity<TrainingSession> bookSession(
            @PathVariable Long customerId, @RequestBody TrainingSession session) {
        TrainingSession created = apiService.bookSession(customerId, session);
        return ResponseEntity.created(URI.create("/api/direct/customers/" + customerId
                + "/sessions/" + created.getId())).body(created);
    }

    @PatchMapping("/customers/{customerId}/sessions/{sessionId}/cancel")
    public TrainingSession cancelSession(
            @PathVariable Long customerId, @PathVariable Long sessionId) {
        return apiService.cancelSession(customerId, sessionId);
    }

    @PatchMapping("/customers/{customerId}/sessions/{sessionId}/reschedule")
    public TrainingSession rescheduleSession(
            @PathVariable Long customerId,
            @PathVariable Long sessionId,
            @RequestBody TrainingSession request) {
        return apiService.rescheduleSession(customerId, sessionId, request);
    }

    @PatchMapping("/customers/{customerId}/sessions/{sessionId}/notes")
    public TrainingSession updateNotes(
            @PathVariable Long customerId,
            @PathVariable Long sessionId,
            @RequestBody TrainingSession request) {
        return apiService.updateNotes(customerId, sessionId, request);
    }

    @PostMapping("/customers/{customerId}/sessions/{sessionId}/review")
    public ResponseEntity<Review> reviewSession(
            @PathVariable Long customerId,
            @PathVariable Long sessionId,
            @RequestBody Review review) {
        Review created = apiService.createReview(customerId, sessionId, review);
        return ResponseEntity.created(URI.create("/api/direct/trainers/"
                + created.getTrainer().getId() + "/reviews/" + created.getId())).body(created);
    }

    @PostMapping("/trainers")
    public ResponseEntity<Trainer> registerTrainer(@RequestBody Trainer trainer) {
        Trainer created = apiService.registerTrainer(trainer);
        return ResponseEntity.created(URI.create("/api/direct/trainers/" + created.getId()))
                .body(created);
    }

    @GetMapping("/trainers")
    public List<Trainer> browseTrainers(@RequestParam(required = false) String category) {
        return apiService.browseTrainers(category);
    }

    @GetMapping("/trainers/{trainerId}")
    public Trainer getTrainer(@PathVariable Long trainerId) {
        return apiService.getTrainer(trainerId);
    }

    @PutMapping("/trainers/{trainerId}")
    public Trainer updateTrainer(
            @PathVariable Long trainerId, @RequestBody Trainer trainer) {
        return apiService.updateTrainer(trainerId, trainer);
    }

    @GetMapping("/trainers/{trainerId}/services")
    public List<TrainingService> trainerServices(@PathVariable Long trainerId) {
        return apiService.trainerServices(trainerId);
    }

    @PostMapping("/trainers/{trainerId}/services")
    public ResponseEntity<TrainingService> createService(
            @PathVariable Long trainerId, @RequestBody TrainingService trainingService) {
        TrainingService created = apiService.createService(trainerId, trainingService);
        return ResponseEntity.created(URI.create("/api/direct/trainers/" + trainerId
                + "/services/" + created.getId())).body(created);
    }

    @PutMapping("/trainers/{trainerId}/services/{serviceId}")
    public TrainingService updateService(
            @PathVariable Long trainerId,
            @PathVariable Long serviceId,
            @RequestBody TrainingService trainingService) {
        return apiService.updateService(trainerId, serviceId, trainingService);
    }

    @GetMapping("/trainers/{trainerId}/timeslots")
    public List<Timeslot> trainerTimeslots(
            @PathVariable Long trainerId,
            @RequestParam(defaultValue = "false") boolean availableOnly) {
        return apiService.trainerTimeslots(trainerId, availableOnly);
    }

    @PostMapping("/trainers/{trainerId}/timeslots")
    public ResponseEntity<Timeslot> createTimeslot(
            @PathVariable Long trainerId, @RequestBody Timeslot timeslot) {
        Timeslot created = apiService.createTimeslot(trainerId, timeslot);
        return ResponseEntity.created(URI.create("/api/direct/trainers/" + trainerId
                + "/timeslots/" + created.getId())).body(created);
    }

    @PutMapping("/trainers/{trainerId}/timeslots/{timeslotId}")
    public Timeslot updateTimeslot(
            @PathVariable Long trainerId,
            @PathVariable Long timeslotId,
            @RequestBody Timeslot timeslot) {
        return apiService.updateTimeslot(trainerId, timeslotId, timeslot);
    }

    @DeleteMapping("/trainers/{trainerId}/timeslots/{timeslotId}")
    public ResponseEntity<Void> deleteTimeslot(
            @PathVariable Long trainerId, @PathVariable Long timeslotId) {
        apiService.deleteTimeslot(trainerId, timeslotId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/trainers/{trainerId}/sessions")
    public List<TrainingSession> trainerSessions(@PathVariable Long trainerId) {
        return apiService.trainerSessions(trainerId);
    }

    @PatchMapping("/trainers/{trainerId}/sessions/{sessionId}/complete")
    public TrainingSession completeSession(
            @PathVariable Long trainerId, @PathVariable Long sessionId) {
        return apiService.completeSession(trainerId, sessionId);
    }

    @GetMapping("/trainers/{trainerId}/reviews")
    public List<Review> trainerReviews(@PathVariable Long trainerId) {
        return apiService.trainerReviews(trainerId);
    }

    @PatchMapping("/trainers/{trainerId}/reviews/{reviewId}/reply")
    public Review replyToReview(
            @PathVariable Long trainerId,
            @PathVariable Long reviewId,
            @RequestBody Review request) {
        return apiService.replyToReview(trainerId, reviewId, request);
    }

    @GetMapping("/trainers/{trainerId}/statistics")
    public Map<String, Object> trainerStatistics(@PathVariable Long trainerId) {
        return apiService.trainerStatistics(trainerId);
    }

    @GetMapping("/services")
    public List<TrainingService> browseServices(
            @RequestParam(required = false) String category) {
        return apiService.browseServices(category);
    }
}
