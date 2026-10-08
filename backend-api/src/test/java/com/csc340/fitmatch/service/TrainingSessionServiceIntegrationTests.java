package com.csc340.fitmatch.service;

import com.csc340.fitmatch.dto.BookSessionRequest;
import com.csc340.fitmatch.dto.CustomerRegistrationRequest;
import com.csc340.fitmatch.dto.ReviewRequest;
import com.csc340.fitmatch.entity.Customer;
import com.csc340.fitmatch.entity.Timeslot;
import com.csc340.fitmatch.entity.Trainer;
import com.csc340.fitmatch.entity.TrainingService;
import com.csc340.fitmatch.repository.CustomerRepository;
import com.csc340.fitmatch.repository.ReviewRepository;
import com.csc340.fitmatch.repository.TimeslotRepository;
import com.csc340.fitmatch.repository.TrainerRepository;
import com.csc340.fitmatch.repository.TrainingServiceRepository;
import com.csc340.fitmatch.repository.TrainingSessionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TrainingSessionServiceIntegrationTests {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TrainingServiceRepository trainingServiceRepository;

    @Autowired
    private TimeslotRepository timeslotRepository;

    @Autowired
    private TrainingSessionRepository sessionRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TrainingSessionService sessionService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void customerRegistrationHashesPasswordAndReturnsSafeProfile() {
        String rawPassword = "Strong-example-password";
        var response = customerService.register(new CustomerRegistrationRequest(
                "Registered Customer", UUID.randomUUID() + "@example.test", rawPassword,
                null, new BigDecimal("80"), new BigDecimal("70"), null, null, null));
        Customer stored = customerRepository.findById(response.id()).orElseThrow();

        assertTrue(passwordEncoder.matches(rawPassword, stored.getPassword()));
        assertFalse(stored.getPassword().equals(rawPassword));
        assertEquals("Registered Customer", response.name());
    }

    @Test
    void trainerDiscoveryIncludesPublishedServiceCategories() {
        Trainer trainer = createTrainer();
        createTrainingService(trainer);

        assertEquals(1, trainerRepository.findMatchingCategory("strength").size());
    }

    @Test
    void cancelledSlotCanBeBookedAgainWithoutLosingSessionHistory() {
        Customer firstCustomer = createCustomer();
        Customer nextCustomer = createCustomer();
        Trainer trainer = createTrainer();
        TrainingService trainingService = createTrainingService(trainer);
        Timeslot timeslot = createTimeslot(trainer);

        var firstBooking = sessionService.book(firstCustomer.getId(),
                new BookSessionRequest(trainingService.getId(), timeslot.getId(), null, null, null));
        assertFalse(timeslotRepository.findById(timeslot.getId()).orElseThrow().getIsAvailable());

        var cancelledBooking = sessionService.cancel(firstCustomer.getId(), firstBooking.id());
        assertTrue(timeslotRepository.findById(timeslot.getId()).orElseThrow().getIsAvailable());
        assertNull(cancelledBooking.timeslotId());
        assertNull(sessionRepository.findById(firstBooking.id()).orElseThrow().getTimeslot());

        var nextBooking = sessionService.book(nextCustomer.getId(),
                new BookSessionRequest(trainingService.getId(), timeslot.getId(), null, null, null));
        assertEquals(2, sessionRepository.findAllByCustomerIdOrderByIdDesc(firstCustomer.getId()).size()
                + sessionRepository.findAllByCustomerIdOrderByIdDesc(nextCustomer.getId()).size());
        assertEquals(timeslot.getId(),
                sessionRepository.findById(nextBooking.id()).orElseThrow().getTimeslot().getId());
    }

    @Test
    void reviewRequiresCompletedSessionAndIsStoredAgainstThatSession() {
        Customer customer = createCustomer();
        Trainer trainer = createTrainer();
        TrainingService trainingService = createTrainingService(trainer);
        Timeslot timeslot = createTimeslot(trainer);
        var booking = sessionService.book(customer.getId(),
                new BookSessionRequest(trainingService.getId(), timeslot.getId(), null, null, null));

        assertThrows(ConflictException.class,
                () -> reviewService.create(customer.getId(), booking.id(), new ReviewRequest(5, "Great")));

        sessionService.markCompleted(trainer.getId(), booking.id());
        reviewService.create(customer.getId(), booking.id(), new ReviewRequest(5, "Great"));
        assertEquals(1, reviewRepository.findAllByTrainerIdOrderByIdDesc(trainer.getId()).size());
    }

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail(UUID.randomUUID() + "@example.test");
        customer.setPassword("hashed-test-password");
        customer.setAccountStatus("ACTIVE");
        customer.setCurrentWeight(new BigDecimal("80.00"));
        customer.setGoalWeight(new BigDecimal("70.00"));
        return customerRepository.saveAndFlush(customer);
    }

    private Trainer createTrainer() {
        Trainer trainer = new Trainer();
        trainer.setName("Test Trainer");
        trainer.setEmail(UUID.randomUUID() + "@example.test");
        trainer.setPassword("hashed-test-password");
        trainer.setAccountStatus("ACTIVE");
        trainer.setYearsOfExperience(4);
        return trainerRepository.saveAndFlush(trainer);
    }

    private TrainingService createTrainingService(Trainer trainer) {
        TrainingService trainingService = new TrainingService();
        trainingService.setTrainer(trainer);
        trainingService.setName("Strength coaching");
        trainingService.setPrice(new BigDecimal("50.00"));
        trainingService.setCategory("Strength");
        trainingService.setStatus("PUBLISHED");
        return trainingServiceRepository.saveAndFlush(trainingService);
    }

    private Timeslot createTimeslot(Trainer trainer) {
        Timeslot timeslot = new Timeslot();
        timeslot.setTrainer(trainer);
        timeslot.setStartTime(LocalDateTime.now().plusDays(1));
        timeslot.setEndTime(LocalDateTime.now().plusDays(1).plusHours(1));
        timeslot.setIsAvailable(true);
        return timeslotRepository.saveAndFlush(timeslot);
    }
}
