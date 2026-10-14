package com.csc340.fitmatch.dto;

import com.csc340.fitmatch.entity.Review;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Integer rating,
        String comments,
        String replyText,
        LocalDateTime reviewDate,
        Long customerId,
        String customerName,
        Long trainerId,
        Long trainingSessionId) {

    public static ReviewResponse from(Review review) {
        return new ReviewResponse(review.getId(), review.getRating(), review.getComments(),
                review.getReplyText(), review.getReviewDate(), review.getCustomer().getId(),
                review.getCustomer().getName(), review.getTrainer().getId(),
                review.getTrainingSession().getId());
    }
}
