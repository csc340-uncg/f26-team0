package com.csc340.fitmatch.dto;

import java.math.BigDecimal;
import java.util.List;

public record TrainerStatisticsResponse(
        long totalSessions,
        long completedSessions,
        long upcomingSessions,
        int customerCount,
        BigDecimal averageRating,
        List<CustomerProgress> customers) {

    public record CustomerProgress(
            Long customerId,
            String customerName,
            BigDecimal currentWeight,
            BigDecimal goalWeight,
            String fitnessGoals,
            long totalSessions,
            long completedSessions,
            String latestNotes) {
    }
}
