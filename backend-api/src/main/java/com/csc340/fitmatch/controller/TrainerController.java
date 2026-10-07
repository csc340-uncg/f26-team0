package com.csc340.fitmatch.controller;

import com.csc340.fitmatch.dto.ReviewReplyRequest;
import com.csc340.fitmatch.dto.ReviewResponse;
import com.csc340.fitmatch.dto.TimeslotRequest;
import com.csc340.fitmatch.dto.TimeslotResponse;
import com.csc340.fitmatch.dto.TrainerRegistrationRequest;
import com.csc340.fitmatch.dto.TrainerResponse;
import com.csc340.fitmatch.dto.TrainerStatisticsResponse;
import com.csc340.fitmatch.dto.TrainerUpdateRequest;
import com.csc340.fitmatch.dto.TrainingServiceRequest;
import com.csc340.fitmatch.dto.TrainingServiceResponse;
import com.csc340.fitmatch.dto.TrainingSessionResponse;
import com.csc340.fitmatch.service.ReviewService;
import com.csc340.fitmatch.service.TimeslotService;
import com.csc340.fitmatch.service.TrainerProfileService;
import com.csc340.fitmatch.service.TrainerStatisticsService;
import com.csc340.fitmatch.service.TrainingCatalogService;
import com.csc340.fitmatch.service.TrainingSessionService;
import jakarta.validation.Valid;
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

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {
    private final TrainerProfileService trainerService;
    private final TrainingCatalogService catalogService;
    private final TimeslotService timeslotService;
    private final TrainingSessionService sessionService;
    private final ReviewService reviewService;
    private final TrainerStatisticsService statisticsService;

    public TrainerController(
            TrainerProfileService trainerService,
            TrainingCatalogService catalogService,
            TimeslotService timeslotService,
            TrainingSessionService sessionService,
            ReviewService reviewService,
            TrainerStatisticsService statisticsService) {
        this.trainerService = trainerService;
        this.catalogService = catalogService;
        this.timeslotService = timeslotService;
        this.sessionService = sessionService;
        this.reviewService = reviewService;
        this.statisticsService = statisticsService;
    }

    @PostMapping
    public ResponseEntity<TrainerResponse> register(
            @Valid @RequestBody TrainerRegistrationRequest request) {
        TrainerResponse trainer = trainerService.register(request);
        return ResponseEntity.created(URI.create("/api/trainers/" + trainer.id())).body(trainer);
    }

    @GetMapping
    public List<TrainerResponse> browse(@RequestParam(required = false) String category) {
        return trainerService.browse(category);
    }

    @GetMapping("/{trainerId}")
    public TrainerResponse getProfile(@PathVariable Long trainerId) {
        return trainerService.getProfile(trainerId);
    }

    @PutMapping("/{trainerId}")
    public TrainerResponse updateProfile(
            @PathVariable Long trainerId,
            @Valid @RequestBody TrainerUpdateRequest request) {
        return trainerService.updateProfile(trainerId, request);
    }

    @GetMapping("/{trainerId}/services")
    public List<TrainingServiceResponse> services(@PathVariable Long trainerId) {
        return catalogService.listPublishedForTrainer(trainerId);
    }

    @PostMapping("/{trainerId}/services")
    public ResponseEntity<TrainingServiceResponse> createService(
            @PathVariable Long trainerId,
            @Valid @RequestBody TrainingServiceRequest request) {
        TrainingServiceResponse trainingService = catalogService.create(trainerId, request);
        return ResponseEntity.created(URI.create("/api/trainers/" + trainerId
                + "/services/" + trainingService.id())).body(trainingService);
    }

    @PutMapping("/{trainerId}/services/{serviceId}")
    public TrainingServiceResponse updateService(
            @PathVariable Long trainerId,
            @PathVariable Long serviceId,
            @Valid @RequestBody TrainingServiceRequest request) {
        return catalogService.update(trainerId, serviceId, request);
    }

    @GetMapping("/{trainerId}/timeslots")
    public List<TimeslotResponse> timeslots(
            @PathVariable Long trainerId,
            @RequestParam(defaultValue = "false") boolean availableOnly) {
        return timeslotService.list(trainerId, availableOnly);
    }

    @PostMapping("/{trainerId}/timeslots")
    public ResponseEntity<TimeslotResponse> createTimeslot(
            @PathVariable Long trainerId,
            @Valid @RequestBody TimeslotRequest request) {
        TimeslotResponse timeslot = timeslotService.create(trainerId, request);
        return ResponseEntity.created(URI.create("/api/trainers/" + trainerId
                + "/timeslots/" + timeslot.id())).body(timeslot);
    }

    @PutMapping("/{trainerId}/timeslots/{timeslotId}")
    public TimeslotResponse updateTimeslot(
            @PathVariable Long trainerId,
            @PathVariable Long timeslotId,
            @Valid @RequestBody TimeslotRequest request) {
        return timeslotService.update(trainerId, timeslotId, request);
    }

    @DeleteMapping("/{trainerId}/timeslots/{timeslotId}")
    public ResponseEntity<Void> deleteTimeslot(
            @PathVariable Long trainerId, @PathVariable Long timeslotId) {
        timeslotService.delete(trainerId, timeslotId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{trainerId}/sessions")
    public List<TrainingSessionResponse> sessions(@PathVariable Long trainerId) {
        return sessionService.listForTrainer(trainerId);
    }

    @PatchMapping("/{trainerId}/sessions/{sessionId}/complete")
    public TrainingSessionResponse completeSession(
            @PathVariable Long trainerId, @PathVariable Long sessionId) {
        return sessionService.markCompleted(trainerId, sessionId);
    }

    @GetMapping("/{trainerId}/reviews")
    public List<ReviewResponse> reviews(@PathVariable Long trainerId) {
        return reviewService.listForTrainer(trainerId);
    }

    @PatchMapping("/{trainerId}/reviews/{reviewId}/reply")
    public ReviewResponse replyToReview(
            @PathVariable Long trainerId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewReplyRequest request) {
        return reviewService.reply(trainerId, reviewId, request);
    }

    @GetMapping("/{trainerId}/statistics")
    public TrainerStatisticsResponse statistics(@PathVariable Long trainerId) {
        return statisticsService.getStatistics(trainerId);
    }
}
