package com.csc340.fitmatch.controller;

import com.csc340.fitmatch.dto.BookSessionRequest;
import com.csc340.fitmatch.dto.CustomerRegistrationRequest;
import com.csc340.fitmatch.dto.CustomerResponse;
import com.csc340.fitmatch.dto.CustomerUpdateRequest;
import com.csc340.fitmatch.dto.RescheduleSessionRequest;
import com.csc340.fitmatch.dto.ReviewRequest;
import com.csc340.fitmatch.dto.ReviewResponse;
import com.csc340.fitmatch.dto.SessionNotesRequest;
import com.csc340.fitmatch.dto.TrainingSessionResponse;
import com.csc340.fitmatch.service.CustomerService;
import com.csc340.fitmatch.service.ReviewService;
import com.csc340.fitmatch.service.TrainingSessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final TrainingSessionService sessionService;
    private final ReviewService reviewService;

    public CustomerController(
            CustomerService customerService,
            TrainingSessionService sessionService,
            ReviewService reviewService) {
        this.customerService = customerService;
        this.sessionService = sessionService;
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> register(
            @Valid @RequestBody CustomerRegistrationRequest request) {
        CustomerResponse customer = customerService.register(request);
        return ResponseEntity.created(URI.create("/api/customers/" + customer.id())).body(customer);
    }

    @GetMapping("/{customerId}")
    public CustomerResponse getProfile(@PathVariable Long customerId) {
        return customerService.getProfile(customerId);
    }

    @PutMapping("/{customerId}")
    public CustomerResponse updateProfile(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerUpdateRequest request) {
        return customerService.updateProfile(customerId, request);
    }

    @GetMapping("/{customerId}/sessions")
    public List<TrainingSessionResponse> sessions(@PathVariable Long customerId) {
        return sessionService.listForCustomer(customerId);
    }

    @PostMapping("/{customerId}/sessions")
    public ResponseEntity<TrainingSessionResponse> bookSession(
            @PathVariable Long customerId,
            @Valid @RequestBody BookSessionRequest request) {
        TrainingSessionResponse session = sessionService.book(customerId, request);
        return ResponseEntity.created(URI.create("/api/customers/" + customerId
                + "/sessions/" + session.id())).body(session);
    }

    @PatchMapping("/{customerId}/sessions/{sessionId}/cancel")
    public TrainingSessionResponse cancelSession(
            @PathVariable Long customerId, @PathVariable Long sessionId) {
        return sessionService.cancel(customerId, sessionId);
    }

    @PatchMapping("/{customerId}/sessions/{sessionId}/reschedule")
    public TrainingSessionResponse rescheduleSession(
            @PathVariable Long customerId,
            @PathVariable Long sessionId,
            @Valid @RequestBody RescheduleSessionRequest request) {
        return sessionService.reschedule(customerId, sessionId, request);
    }

    @PatchMapping("/{customerId}/sessions/{sessionId}/notes")
    public TrainingSessionResponse updateSessionNotes(
            @PathVariable Long customerId,
            @PathVariable Long sessionId,
            @Valid @RequestBody SessionNotesRequest request) {
        return sessionService.updateNotes(customerId, sessionId, request);
    }

    @PostMapping("/{customerId}/sessions/{sessionId}/review")
    public ResponseEntity<ReviewResponse> reviewSession(
            @PathVariable Long customerId,
            @PathVariable Long sessionId,
            @Valid @RequestBody ReviewRequest request) {
        ReviewResponse review = reviewService.create(customerId, sessionId, request);
        return ResponseEntity.created(URI.create("/api/trainers/" + review.trainerId()
                + "/reviews/" + review.id())).body(review);
    }
}
