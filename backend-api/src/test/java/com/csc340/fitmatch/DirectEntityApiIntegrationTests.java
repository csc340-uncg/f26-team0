package com.csc340.fitmatch;

import com.csc340.fitmatch.entity.Customer;
import com.csc340.fitmatch.entity.Timeslot;
import com.csc340.fitmatch.entity.Trainer;
import com.csc340.fitmatch.entity.TrainingService;
import com.csc340.fitmatch.repository.CustomerRepository;
import com.csc340.fitmatch.repository.TimeslotRepository;
import com.csc340.fitmatch.repository.TrainerRepository;
import com.csc340.fitmatch.repository.TrainingServiceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DirectEntityApiIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TrainingServiceRepository trainingServiceRepository;

    @Autowired
    private TimeslotRepository timeslotRepository;

    @Test
    void customerRegistrationAcceptsEntityAndDoesNotExposePassword() throws Exception {
        String email = UUID.randomUUID() + "@example.test";
        mockMvc.perform(post("/api/direct/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Direct Customer",
                                  "email": "%s",
                                  "password": "Strong-example-password",
                                  "currentWeight": 80,
                                  "goalWeight": 70
                                }
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.password").doesNotExist());

        Customer customer = customerRepository.findAll().stream()
                .filter(candidate -> candidate.getEmail().equals(email))
                .findFirst().orElseThrow();
        mockMvc.perform(get("/api/direct/customers/{customerId}", customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.trainingSessions").doesNotExist())
                .andExpect(jsonPath("$.reviews").doesNotExist());
    }

    @Test
    void bookingAcceptsEntityRelationshipsByIdAndSerializesWithoutCycles() throws Exception {
        Customer customer = new Customer();
        customer.setName("Direct Booking Customer");
        customer.setEmail(UUID.randomUUID() + "@example.test");
        customer.setPassword("hashed-test-password");
        customer.setAccountStatus("ACTIVE");
        customer.setCurrentWeight(new BigDecimal("80"));
        customer.setGoalWeight(new BigDecimal("70"));
        customerRepository.saveAndFlush(customer);

        Trainer trainer = new Trainer();
        trainer.setName("Direct Booking Trainer");
        trainer.setEmail(UUID.randomUUID() + "@example.test");
        trainer.setPassword("hashed-test-password");
        trainer.setAccountStatus("ACTIVE");
        trainer.setYearsOfExperience(4);
        trainerRepository.saveAndFlush(trainer);

        TrainingService trainingService = new TrainingService();
        trainingService.setTrainer(trainer);
        trainingService.setName("Strength Coaching");
        trainingService.setPrice(new BigDecimal("25"));
        trainingService.setCategory("Strength");
        trainingService.setStatus("PUBLISHED");
        trainingServiceRepository.saveAndFlush(trainingService);

        Timeslot timeslot = new Timeslot();
        timeslot.setTrainer(trainer);
        timeslot.setStartTime(LocalDateTime.now().plusDays(2));
        timeslot.setEndTime(LocalDateTime.now().plusDays(2).plusHours(1));
        timeslot.setIsAvailable(true);
        timeslotRepository.saveAndFlush(timeslot);

        mockMvc.perform(post("/api/direct/customers/{customerId}/sessions", customer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "trainingService": { "id": %d },
                                  "timeslot": { "id": %d },
                                  "notes": "Focus on mobility"
                                }
                                """.formatted(trainingService.getId(), timeslot.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("BOOKED")))
                .andExpect(jsonPath("$.customer.id", is(customer.getId().intValue())))
                .andExpect(jsonPath("$.trainingService.id", is(trainingService.getId().intValue())))
                .andExpect(jsonPath("$.timeslot.id", is(timeslot.getId().intValue())))
                .andExpect(jsonPath("$.customer.password").doesNotExist())
                .andExpect(jsonPath("$.customer.trainingSessions").doesNotExist())
                .andExpect(jsonPath("$.trainingService.trainingSessions").doesNotExist())
                .andExpect(jsonPath("$.timeslot.trainingSession").doesNotExist());
    }
}
