package com.csc340.fitmatch.service;

import com.csc340.fitmatch.entity.Customer;
import com.csc340.fitmatch.entity.Review;
import com.csc340.fitmatch.entity.Timeslot;
import com.csc340.fitmatch.entity.Trainer;
import com.csc340.fitmatch.entity.TrainingService;
import com.csc340.fitmatch.entity.TrainingSession;
import com.csc340.fitmatch.repository.CustomerRepository;
import com.csc340.fitmatch.repository.ReviewRepository;
import com.csc340.fitmatch.repository.TimeslotRepository;
import com.csc340.fitmatch.repository.TrainerRepository;
import com.csc340.fitmatch.repository.TrainingServiceRepository;
import com.csc340.fitmatch.repository.TrainingSessionRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class DirectEntityApiService {
    private static final Set<String> SERVICE_STATUSES = Set.of("DRAFT", "PUBLISHED", "ARCHIVED");

    private final CustomerRepository customerRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingServiceRepository trainingServiceRepository;
    private final TimeslotRepository timeslotRepository;
    private final TrainingSessionRepository sessionRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    public DirectEntityApiService(
            CustomerRepository customerRepository,
            TrainerRepository trainerRepository,
            TrainingServiceRepository trainingServiceRepository,
            TimeslotRepository timeslotRepository,
            TrainingSessionRepository sessionRepository,
            ReviewRepository reviewRepository,
            PasswordEncoder passwordEncoder,
            Validator validator) {
        this.customerRepository = customerRepository;
        this.trainerRepository = trainerRepository;
        this.trainingServiceRepository = trainingServiceRepository;
        this.timeslotRepository = timeslotRepository;
        this.sessionRepository = sessionRepository;
        this.reviewRepository = reviewRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    public Customer registerCustomer(Customer request) {
        Customer customer = new Customer();
        customer.setName(request.getName() == null ? null : request.getName().trim());
        customer.setEmail(normalizeEmail(request.getEmail()));
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setPassword(encodePassword(request.getPassword()));
        customer.setAccountStatus("ACTIVE");
        customer.setCurrentWeight(request.getCurrentWeight());
        customer.setGoalWeight(request.getGoalWeight());
        customer.setFitnessLevel(request.getFitnessLevel());
        customer.setFitnessGoals(request.getFitnessGoals());
        customer.setInjuriesOrHealthConcerns(request.getInjuriesOrHealthConcerns());
        validate(customer);
        ensureCustomerEmailAvailable(customer.getEmail());
        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public Customer getCustomer(Long customerId) {
        return findCustomer(customerId);
    }

    public Customer updateCustomer(Long customerId, Customer request) {
        Customer customer = findCustomer(customerId);
        String email = normalizeEmail(request.getEmail());
        if (!customer.getEmail().equalsIgnoreCase(email)) {
            ensureCustomerEmailAvailable(email);
        }
        customer.setName(request.getName() == null ? null : request.getName().trim());
        customer.setEmail(email);
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setCurrentWeight(request.getCurrentWeight());
        customer.setGoalWeight(request.getGoalWeight());
        customer.setFitnessLevel(request.getFitnessLevel());
        customer.setFitnessGoals(request.getFitnessGoals());
        customer.setInjuriesOrHealthConcerns(request.getInjuriesOrHealthConcerns());
        validate(customer);
        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public List<TrainingSession> customerSessions(Long customerId) {
        requireCustomer(customerId);
        return sessionRepository.findAllByCustomerIdOrderByIdDesc(customerId);
    }

    @Transactional(readOnly = true)
    public List<Review> customerReviews(Long customerId) {
        requireCustomer(customerId);
        return reviewRepository.findAllByCustomerIdOrderByReviewDateDescIdDesc(customerId);
    }

    public TrainingSession bookSession(Long customerId, TrainingSession request) {
        Customer customer = findCustomer(customerId);
        Long serviceId = request.getTrainingService() == null
                ? null : request.getTrainingService().getId();
        Long timeslotId = request.getTimeslot() == null ? null : request.getTimeslot().getId();
        TrainingService trainingService = findTrainingService(serviceId);
        if (!"PUBLISHED".equalsIgnoreCase(trainingService.getStatus())) {
            throw new ConflictException("Only published services can be booked.");
        }
        Timeslot timeslot = lockTimeslot(timeslotId);
        ensureBookable(timeslot);
        if (!timeslot.getTrainer().getId().equals(trainingService.getTrainer().getId())) {
            throw new BadRequestException("The selected timeslot does not belong to the service's trainer.");
        }

        TrainingSession session = new TrainingSession();
        session.setCustomer(customer);
        session.setTrainingService(trainingService);
        session.setTimeslot(timeslot);
        session.setNotes(request.getNotes());
        session.setLevel(request.getLevel());
        session.setLocation(request.getLocation());
        session.setStatus("BOOKED");
        timeslot.setIsAvailable(false);
        return sessionRepository.save(session);
    }

    public TrainingSession cancelSession(Long customerId, Long sessionId) {
        TrainingSession session = lockSession(sessionId);
        ensureCustomerOwns(session, customerId);
        ensureBooked(session);
        Timeslot timeslot = lockTimeslot(session.getTimeslot().getId());
        timeslot.setIsAvailable(true);
        session.setStatus("CANCELLED");
        session.setTimeslot(null);
        return sessionRepository.save(session);
    }

    public TrainingSession rescheduleSession(
            Long customerId, Long sessionId, TrainingSession request) {
        TrainingSession session = lockSession(sessionId);
        ensureCustomerOwns(session, customerId);
        ensureBooked(session);
        Long timeslotId = request.getTimeslot() == null ? null : request.getTimeslot().getId();
        Timeslot newTimeslot = lockTimeslot(timeslotId);
        ensureBookable(newTimeslot);
        if (!newTimeslot.getTrainer().getId().equals(session.getTrainingService().getTrainer().getId())) {
            throw new BadRequestException("The new timeslot must belong to the session's trainer.");
        }
        Timeslot oldTimeslot = lockTimeslot(session.getTimeslot().getId());
        oldTimeslot.setIsAvailable(true);
        newTimeslot.setIsAvailable(false);
        session.setTimeslot(newTimeslot);
        return sessionRepository.save(session);
    }

    public TrainingSession updateNotes(
            Long customerId, Long sessionId, TrainingSession request) {
        TrainingSession session = lockSession(sessionId);
        ensureCustomerOwns(session, customerId);
        if ("CANCELLED".equalsIgnoreCase(session.getStatus())) {
            throw new ConflictException("Notes cannot be updated for a cancelled session.");
        }
        session.setNotes(request.getNotes() == null ? null : request.getNotes().trim());
        validate(session);
        return sessionRepository.save(session);
    }

    public Review createReview(Long customerId, Long sessionId, Review request) {
        TrainingSession session = findSession(sessionId);
        if (!session.getCustomer().getId().equals(customerId)) {
            throw new NotFoundException("Training session " + sessionId
                    + " was not found for customer " + customerId + ".");
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
        review.setRating(request.getRating());
        review.setComments(request.getComments());
        review.setReviewDate(LocalDateTime.now());
        validate(review);
        session.setReview(review);
        return reviewRepository.save(review);
    }

    public Trainer registerTrainer(Trainer request) {
        Trainer trainer = new Trainer();
        trainer.setName(request.getName() == null ? null : request.getName().trim());
        trainer.setEmail(normalizeEmail(request.getEmail()));
        trainer.setPassword(encodePassword(request.getPassword()));
        trainer.setAccountStatus("ACTIVE");
        trainer.setBiography(request.getBiography());
        trainer.setCertifications(request.getCertifications());
        trainer.setYearsOfExperience(request.getYearsOfExperience());
        trainer.setSpecialties(request.getSpecialties());
        validate(trainer);
        ensureTrainerEmailAvailable(trainer.getEmail());
        return trainerRepository.save(trainer);
    }

    @Transactional(readOnly = true)
    public List<Trainer> browseTrainers(String category) {
        return category == null || category.isBlank()
                ? trainerRepository.findAll()
                : trainerRepository.findMatchingCategory(category.trim());
    }

    @Transactional(readOnly = true)
    public Trainer getTrainer(Long trainerId) {
        return findTrainer(trainerId);
    }

    public Trainer updateTrainer(Long trainerId, Trainer request) {
        Trainer trainer = findTrainer(trainerId);
        String email = normalizeEmail(request.getEmail());
        if (!trainer.getEmail().equalsIgnoreCase(email)) {
            ensureTrainerEmailAvailable(email);
        }
        trainer.setName(request.getName() == null ? null : request.getName().trim());
        trainer.setEmail(email);
        trainer.setBiography(request.getBiography());
        trainer.setCertifications(request.getCertifications());
        trainer.setYearsOfExperience(request.getYearsOfExperience());
        trainer.setSpecialties(request.getSpecialties());
        validate(trainer);
        return trainerRepository.save(trainer);
    }

    @Transactional(readOnly = true)
    public List<TrainingService> trainerServices(Long trainerId) {
        requireTrainer(trainerId);
        return trainingServiceRepository
                .findAllByTrainerIdAndStatusIgnoreCaseOrderById(trainerId, "PUBLISHED");
    }

    public TrainingService createService(Long trainerId, TrainingService request) {
        TrainingService trainingService = new TrainingService();
        trainingService.setTrainer(findTrainer(trainerId));
        applyServiceFields(trainingService, request, "PUBLISHED");
        validate(trainingService);
        return trainingServiceRepository.save(trainingService);
    }

    public TrainingService updateService(
            Long trainerId, Long serviceId, TrainingService request) {
        TrainingService trainingService = findTrainingService(serviceId);
        if (!trainingService.getTrainer().getId().equals(trainerId)) {
            throw new NotFoundException("Training service " + serviceId
                    + " was not found for trainer " + trainerId + ".");
        }
        applyServiceFields(trainingService, request, trainingService.getStatus());
        validate(trainingService);
        return trainingServiceRepository.save(trainingService);
    }

    @Transactional(readOnly = true)
    public List<Timeslot> trainerTimeslots(Long trainerId, boolean availableOnly) {
        requireTrainer(trainerId);
        return availableOnly
                ? timeslotRepository.findAllByTrainerIdAndIsAvailableTrueOrderByStartTime(trainerId)
                : timeslotRepository.findAllByTrainerIdOrderByStartTime(trainerId);
    }

    public Timeslot createTimeslot(Long trainerId, Timeslot request) {
        Timeslot timeslot = new Timeslot();
        timeslot.setTrainer(findTrainer(trainerId));
        applyTimeslotFields(timeslot, request);
        validateTimeslotRange(timeslot);
        validate(timeslot);
        return timeslotRepository.save(timeslot);
    }

    public Timeslot updateTimeslot(
            Long trainerId, Long timeslotId, Timeslot request) {
        Timeslot timeslot = lockTimeslot(timeslotId);
        if (!timeslot.getTrainer().getId().equals(trainerId)) {
            throw new NotFoundException("Timeslot " + timeslotId
                    + " was not found for trainer " + trainerId + ".");
        }
        if (timeslot.getTrainingSession() != null) {
            throw new ConflictException("A booked timeslot cannot be edited.");
        }
        applyTimeslotFields(timeslot, request);
        validateTimeslotRange(timeslot);
        validate(timeslot);
        return timeslotRepository.save(timeslot);
    }

    public void deleteTimeslot(Long trainerId, Long timeslotId) {
        Timeslot timeslot = lockTimeslot(timeslotId);
        if (!timeslot.getTrainer().getId().equals(trainerId)) {
            throw new NotFoundException("Timeslot " + timeslotId
                    + " was not found for trainer " + trainerId + ".");
        }
        if (timeslot.getTrainingSession() != null) {
            throw new ConflictException("A booked timeslot cannot be deleted.");
        }
        timeslotRepository.delete(timeslot);
    }

    @Transactional(readOnly = true)
    public List<TrainingSession> trainerSessions(Long trainerId) {
        requireTrainer(trainerId);
        return sessionRepository.findAllByTrainingService_Trainer_IdOrderByIdDesc(trainerId);
    }

    public TrainingSession completeSession(Long trainerId, Long sessionId) {
        TrainingSession session = lockSession(sessionId);
        if (!session.getTrainingService().getTrainer().getId().equals(trainerId)) {
            throw new NotFoundException("Training session " + sessionId
                    + " was not found for trainer " + trainerId + ".");
        }
        ensureBooked(session);
        session.setStatus("COMPLETED");
        return sessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public List<Review> trainerReviews(Long trainerId) {
        requireTrainer(trainerId);
        return reviewRepository.findAllByTrainerIdOrderByReviewDateDescIdDesc(trainerId);
    }

    public Review replyToReview(Long trainerId, Long reviewId, Review request) {
        requireTrainer(trainerId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review " + reviewId + " was not found."));
        if (!review.getTrainer().getId().equals(trainerId)) {
            throw new NotFoundException("Review " + reviewId
                    + " was not found for trainer " + trainerId + ".");
        }
        review.setReplyText(request.getReplyText() == null ? null : request.getReplyText().trim());
        validate(review);
        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> trainerStatistics(Long trainerId) {
        requireTrainer(trainerId);
        List<TrainingSession> sessions =
                sessionRepository.findAllByTrainingService_Trainer_IdOrderByIdDesc(trainerId);
        List<Review> reviews = reviewRepository.findAllByTrainerIdOrderByReviewDateDescIdDesc(trainerId);
        LocalDateTime now = LocalDateTime.now();
        long completed = sessions.stream()
                .filter(session -> "COMPLETED".equalsIgnoreCase(session.getStatus()))
                .count();
        long upcoming = sessions.stream()
                .filter(session -> "BOOKED".equalsIgnoreCase(session.getStatus()))
                .filter(session -> session.getTimeslot() != null
                        && session.getTimeslot().getStartTime().isAfter(now))
                .count();
        Map<Long, List<TrainingSession>> byCustomer = sessions.stream()
                .collect(Collectors.groupingBy(session -> session.getCustomer().getId()));
        List<Map<String, Object>> progress = byCustomer.values().stream()
                .map(this::customerProgress)
                .sorted(Comparator.comparing(row -> (String) row.get("customerName")))
                .toList();
        BigDecimal averageRating = reviews.isEmpty()
                ? null
                : BigDecimal.valueOf(reviews.stream().mapToInt(Review::getRating)
                        .average().orElseThrow())
                        .setScale(2, RoundingMode.HALF_UP);

        Map<String, Object> statistics = new LinkedHashMap<>();
        statistics.put("totalSessions", sessions.size());
        statistics.put("completedSessions", completed);
        statistics.put("upcomingSessions", upcoming);
        statistics.put("uniqueCustomers", byCustomer.size());
        statistics.put("averageRating", averageRating);
        statistics.put("customerProgress", progress);
        return statistics;
    }

    @Transactional(readOnly = true)
    public List<TrainingService> browseServices(String category) {
        if (category == null || category.isBlank()) {
            return trainingServiceRepository.findAllByStatusIgnoreCaseOrderById("PUBLISHED");
        }
        return trainingServiceRepository
                .findAllByCategoryIgnoreCaseAndStatusIgnoreCaseOrderById(
                        category.trim(), "PUBLISHED");
    }

    private Map<String, Object> customerProgress(List<TrainingSession> sessions) {
        TrainingSession latest = sessions.stream()
                .max(Comparator.comparing(session -> session.getTimeslot() == null
                        ? LocalDateTime.MIN : session.getTimeslot().getStartTime()))
                .orElseThrow();
        Customer customer = latest.getCustomer();
        long completed = sessions.stream()
                .filter(session -> "COMPLETED".equalsIgnoreCase(session.getStatus()))
                .count();
        Map<String, Object> progress = new LinkedHashMap<>();
        progress.put("customerId", customer.getId());
        progress.put("customerName", customer.getName());
        progress.put("currentWeight", customer.getCurrentWeight());
        progress.put("goalWeight", customer.getGoalWeight());
        progress.put("fitnessGoals", customer.getFitnessGoals());
        progress.put("totalSessions", sessions.size());
        progress.put("completedSessions", completed);
        progress.put("latestNotes", latest.getNotes());
        return progress;
    }

    private void applyServiceFields(
            TrainingService trainingService, TrainingService request, String existingStatus) {
        trainingService.setName(request.getName() == null ? null : request.getName().trim());
        trainingService.setDescription(request.getDescription());
        trainingService.setPrice(request.getPrice());
        trainingService.setCategory(request.getCategory() == null ? null : request.getCategory().trim());
        String requestedStatus = request.getStatus();
        String status = requestedStatus == null || requestedStatus.isBlank()
                ? existingStatus : requestedStatus;
        if (!SERVICE_STATUSES.contains(status.trim().toUpperCase(Locale.ROOT))) {
            throw new BadRequestException("Service status must be DRAFT, PUBLISHED, or ARCHIVED.");
        }
        trainingService.setStatus(status.trim().toUpperCase(Locale.ROOT));
    }

    private void applyTimeslotFields(Timeslot timeslot, Timeslot request) {
        timeslot.setStartTime(request.getStartTime());
        timeslot.setEndTime(request.getEndTime());
        timeslot.setIsAvailable(request.getIsAvailable());
    }

    private void validateTimeslotRange(Timeslot timeslot) {
        if (timeslot.getStartTime() == null || timeslot.getEndTime() == null) {
            return;
        }
        if (!timeslot.getEndTime().isAfter(timeslot.getStartTime())) {
            throw new BadRequestException("endTime must be after startTime.");
        }
        if (!timeslot.getStartTime().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Timeslots must start in the future.");
        }
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
        if (!"BOOKED".equalsIgnoreCase(session.getStatus())) {
            throw new ConflictException("Only booked sessions can be changed.");
        }
    }

    private void ensureCustomerOwns(TrainingSession session, Long customerId) {
        if (!session.getCustomer().getId().equals(customerId)) {
            throw new NotFoundException("Training session " + session.getId()
                    + " was not found for customer " + customerId + ".");
        }
    }

    private <T> void validate(T entity) {
        Set<ConstraintViolation<T>> violations = validator.validate(entity);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private String encodePassword(String password) {
        if (password != null && password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BadRequestException("Password must be no more than 72 UTF-8 bytes.");
        }
        if (password != null && password.length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters.");
        }
        return password == null ? null : passwordEncoder.encode(password);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private void ensureCustomerEmailAvailable(String email) {
        if (email != null && customerRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("An account with that email already exists.");
        }
    }

    private void ensureTrainerEmailAvailable(String email) {
        if (email != null && trainerRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("An account with that email already exists.");
        }
    }

    private Customer findCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException(
                        "Customer " + customerId + " was not found."));
    }

    private void requireCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new NotFoundException("Customer " + customerId + " was not found.");
        }
    }

    private Trainer findTrainer(Long trainerId) {
        return trainerRepository.findById(trainerId)
                .orElseThrow(() -> new NotFoundException(
                        "Trainer " + trainerId + " was not found."));
    }

    private void requireTrainer(Long trainerId) {
        if (!trainerRepository.existsById(trainerId)) {
            throw new NotFoundException("Trainer " + trainerId + " was not found.");
        }
    }

    private TrainingService findTrainingService(Long serviceId) {
        if (serviceId == null) {
            throw new BadRequestException("A training service ID is required.");
        }
        return trainingServiceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException(
                        "Training service " + serviceId + " was not found."));
    }

    private TrainingSession findSession(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException(
                        "Training session " + sessionId + " was not found."));
    }

    private TrainingSession lockSession(Long sessionId) {
        return sessionRepository.findByIdForUpdate(sessionId)
                .orElseThrow(() -> new NotFoundException(
                        "Training session " + sessionId + " was not found."));
    }

    private Timeslot lockTimeslot(Long timeslotId) {
        if (timeslotId == null) {
            throw new BadRequestException("A timeslot ID is required.");
        }
        return timeslotRepository.findByIdForUpdate(timeslotId)
                .orElseThrow(() -> new NotFoundException(
                        "Timeslot " + timeslotId + " was not found."));
    }
}
