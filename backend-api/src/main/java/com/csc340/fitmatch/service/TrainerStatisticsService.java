package com.csc340.fitmatch.service;

import com.csc340.fitmatch.dto.TrainerStatisticsResponse;
import com.csc340.fitmatch.entity.Review;
import com.csc340.fitmatch.entity.TrainingSession;
import com.csc340.fitmatch.repository.ReviewRepository;
import com.csc340.fitmatch.repository.TrainingSessionRepository;
import com.csc340.fitmatch.repository.TrainerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TrainerStatisticsService {
        private final TrainerRepository trainerRepository;
        private final TrainingSessionRepository sessionRepository;
        private final ReviewRepository reviewRepository;

        public TrainerStatisticsService(
                        TrainerRepository trainerRepository,
                        TrainingSessionRepository sessionRepository,
                        ReviewRepository reviewRepository) {
                this.trainerRepository = trainerRepository;
                this.sessionRepository = sessionRepository;
                this.reviewRepository = reviewRepository;
        }

        public TrainerStatisticsResponse getStatistics(Long trainerId) {
                if (!trainerRepository.existsById(trainerId)) {
                        throw new NotFoundException("Trainer " + trainerId + " was not found.");
                }
                List<TrainingSession> sessions = sessionRepository
                                .findAllByTrainingService_Trainer_IdOrderByIdDesc(trainerId);
                List<Review> reviews = reviewRepository.findAllByTrainerIdOrderByIdDesc(trainerId);
                LocalDateTime now = LocalDateTime.now();
                long completedCount = sessions.stream()
                                .filter(session -> "COMPLETED".equalsIgnoreCase(session.getStatus()))
                                .count();
                long upcomingCount = sessions.stream()
                                .filter(session -> "BOOKED".equalsIgnoreCase(session.getStatus()))
                                .filter(session -> session.getTimeslot().getStartTime().isAfter(now))
                                .count();
                Map<Long, List<TrainingSession>> sessionsByCustomer = sessions.stream()
                                .collect(Collectors.groupingBy(session -> session.getCustomer().getId()));
                List<TrainerStatisticsResponse.CustomerProgress> progress = sessionsByCustomer.values().stream()
                                .map(this::toProgress)
                                .sorted(Comparator.comparing(TrainerStatisticsResponse.CustomerProgress::customerName))
                                .toList();
                BigDecimal averageRating = reviews.isEmpty()
                                ? null
                                : BigDecimal.valueOf(reviews.stream().mapToInt(Review::getRating)
                                                .average().orElseThrow())
                                                .setScale(2, RoundingMode.HALF_UP);

                return new TrainerStatisticsResponse(
                                sessions.size(), completedCount, upcomingCount, sessionsByCustomer.size(),
                                averageRating, progress);
        }

        private TrainerStatisticsResponse.CustomerProgress toProgress(List<TrainingSession> sessions) {
                TrainingSession latest = sessions.stream()
                                .max(Comparator.comparing(session -> session.getTimeslot().getStartTime()))
                                .orElseThrow();
                var customer = latest.getCustomer();
                long completedCount = sessions.stream()
                                .filter(session -> "COMPLETED".equalsIgnoreCase(session.getStatus()))
                                .count();
                return new TrainerStatisticsResponse.CustomerProgress(
                                customer.getId(), customer.getName(), customer.getCurrentWeight(),
                                customer.getGoalWeight(),
                                customer.getFitnessGoals(), sessions.size(), completedCount, latest.getNotes());
        }
}
