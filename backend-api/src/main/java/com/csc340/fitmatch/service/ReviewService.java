package com.csc340.fitmatch.service;

import com.csc340.fitmatch.dto.ReviewReplyRequest;
import com.csc340.fitmatch.dto.ReviewRequest;
import com.csc340.fitmatch.dto.ReviewResponse;
import com.csc340.fitmatch.entity.Review;
import com.csc340.fitmatch.entity.TrainingSession;
import com.csc340.fitmatch.repository.ReviewRepository;
import com.csc340.fitmatch.repository.TrainingSessionRepository;
import com.csc340.fitmatch.repository.TrainerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final TrainingSessionRepository sessionRepository;
    private final TrainerRepository trainerRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            TrainingSessionRepository sessionRepository,
            TrainerRepository trainerRepository) {
        this.reviewRepository = reviewRepository;
        this.sessionRepository = sessionRepository;
        this.trainerRepository = trainerRepository;
    }

    public ReviewResponse create(Long customerId, Long sessionId, ReviewRequest request) {
        TrainingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException(
                        "Training session " + sessionId + " was not found."));
        if (!session.getCustomer().getId().equals(customerId)) {
            throw new NotFoundException("Training session " + sessionId + " was not found for customer "
                    + customerId + ".");
        }
        if (!"COMPLETED".equalsIgnoreCase(session.getStatus())) {
            throw new ConflictException("A review can only be submitted for a completed session.");
        }
        if (session.getReview() != null) {
            throw new ConflictException("A review already exists for this training session.");
        }

        Review review = new Review();
        review.setTrainingSession(session);
        review.setCustomer(session.getCustomer());
        review.setTrainer(session.getTrainingService().getTrainer());
        review.setRating(request.rating());
        review.setComments(request.comments());
        session.setReview(review);
        return ReviewResponse.from(reviewRepository.save(review));
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> listForTrainer(Long trainerId) {
        if (!trainerRepository.existsById(trainerId)) {
            throw new NotFoundException("Trainer " + trainerId + " was not found.");
        }
        return reviewRepository.findAllByTrainerIdOrderByIdDesc(trainerId)
                .stream().map(ReviewResponse::from).toList();
    }

    public ReviewResponse reply(Long trainerId, Long reviewId, ReviewReplyRequest request) {
        if (!trainerRepository.existsById(trainerId)) {
            throw new NotFoundException("Trainer " + trainerId + " was not found.");
        }
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review " + reviewId + " was not found."));
        if (!review.getTrainer().getId().equals(trainerId)) {
            throw new NotFoundException("Review " + reviewId + " was not found for trainer "
                    + trainerId + ".");
        }
        review.setReplyText(request.replyText().trim());
        return ReviewResponse.from(reviewRepository.save(review));
    }
}
