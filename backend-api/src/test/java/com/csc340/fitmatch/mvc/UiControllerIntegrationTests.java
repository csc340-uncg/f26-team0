package com.csc340.fitmatch.mvc;

import com.csc340.fitmatch.service.TimeslotService;
import com.csc340.fitmatch.service.TrainingCatalogService;
import com.csc340.fitmatch.service.TrainingSessionService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
class UiControllerIntegrationTests {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private TrainingCatalogService trainingCatalogService;

    @Autowired
    private TimeslotService timeslotService;

    @Autowired
    private TrainingSessionService trainingSessionService;

    @Autowired
    private TrainerUiController trainerUiController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void customerAndTrainerFlowsRenderAndUseExistingServices() throws Exception {
        page("/", null);
        page("/login", null);
        page("/register", null);
        page("/provider/register", null);
        page("/customers/browse-trainers", null);
        mockMvc.perform(get("/customers/dashboard"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));

        String suffix = UUID.randomUUID().toString();
        MockHttpSession trainerSession = register(
                "/provider/register",
                "trainer-" + suffix + "@example.com",
                "Trainer Demo",
                "StrongPassword1",
                "trainer");
        long trainerId = (Long) trainerSession.getAttribute("userId");

        page("/trainers/dashboard", trainerSession);
        page("/trainers/provider-dashboard", trainerSession);
        page("/trainers/profile", trainerSession);
        page("/trainers/edit-details", trainerSession);
        page("/trainers/manage-services", trainerSession);
        page("/trainers/new-service", trainerSession);
        page("/trainers/view-clients", trainerSession);

        mockMvc.perform(post("/trainers/services").session(trainerSession)
                        .param("name", "Demo Strength")
                        .param("description", "A demo training service")
                        .param("price", "45.00")
                        .param("category", "Strength")
                        .param("status", "PUBLISHED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trainers/manage-services"));
        page("/trainers/manage-services", trainerSession);

        DateTimeFormatter formDate = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime start = LocalDateTime.now().plusDays(2).withSecond(0).withNano(0);
        mockMvc.perform(post("/trainers/timeslots").session(trainerSession)
                        .param("startTime", start.format(formDate))
                        .param("endTime", start.plusHours(1).format(formDate))
                        .param("isAvailable", "true"))
                .andExpect(status().is3xxRedirection());

        register(
                "/register",
                "customer-" + suffix + "@example.com",
                "Customer Demo",
                "StrongPassword1",
                "customer");
        var loginResult = mockMvc.perform(post("/login")
                        .param("role", "customer")
                        .param("email", "customer-" + suffix + "@example.com")
                        .param("password", "StrongPassword1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers/dashboard"))
                .andReturn();
        HttpSession loggedInSession = loginResult.getRequest().getSession(false);
        if (!(loggedInSession instanceof MockHttpSession customerSession)) {
            throw new AssertionError("Login did not create a session.");
        }
        long customerId = (Long) customerSession.getAttribute("userId");

        page("/customers/dashboard", customerSession);
        page("/customers/profile", customerSession);
        page("/customers/edit-details", customerSession);
        page("/customers/my-sessions", customerSession);
        page("/customers/trainers/" + trainerId, customerSession);
        page("/trainers/edit-service/" + trainingCatalogService.listForTrainer(trainerId).get(0).id(),
                trainerSession);

        long serviceId = trainingCatalogService.listForTrainer(trainerId).get(0).id();
        long timeslotId = timeslotService.list(trainerId, true).get(0).id();
        mockMvc.perform(post("/customers/book").session(customerSession)
                        .param("trainingServiceId", Long.toString(serviceId))
                        .param("timeslotId", Long.toString(timeslotId))
                        .param("notes", "Demo goal")
                        .param("level", "Beginner")
                        .param("location", "Gym"))
                .andExpect(status().is3xxRedirection());

        long sessionId = trainingSessionService.listForCustomer(customerId).get(0).id();
        page("/customers/sessions/" + sessionId, customerSession);
        page("/trainers/clients/" + customerId, trainerSession);
        mockMvc.perform(post("/trainers/sessions/" + sessionId + "/complete")
                        .session(trainerSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trainers/dashboard"));
        assertEquals("COMPLETED", trainingSessionService.listForCustomer(customerId).get(0).status());
        page("/customers/sessions/" + sessionId + "/review", customerSession);
        mockMvc.perform(post("/customers/sessions/" + sessionId + "/review")
                        .session(customerSession)
                        .param("rating", "5")
                        .param("comments", "Great session"))
                .andExpect(status().is3xxRedirection());
        page("/customers/my-sessions?status=completed", customerSession);
        page("/trainers/dashboard", trainerSession);
    }

    @Test
    void trainerPictureCanBeUploadedAndStreamsWithPlaceholderWhenMissing() throws Exception {
        byte[] picture = {1, 2, 3, 4};
        String suffix = UUID.randomUUID().toString();
        var registration = mockMvc.perform(multipart("/provider/register")
                        .file(new MockMultipartFile(
                                "profilePicture", "trainer.png", "image/png", picture))
                        .param("email", "picture-" + suffix + "@example.com")
                        .param("name", "Picture Trainer")
                        .param("password", "StrongPassword1")
                        .param("yearsOfExperience", "4")
                        .param("specialties", "Strength")
                        .param("biography", "Picture test")
                        .param("certifications", "CPT"))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        HttpSession uploadedSession = registration.getRequest().getSession(false);
        if (!(uploadedSession instanceof MockHttpSession trainerSession)) {
            throw new AssertionError("Registration did not create a session.");
        }
        Long trainerId = (Long) trainerSession.getAttribute("userId");

        var pictureResponse = trainerUiController.profilePicture(trainerId);
        assertEquals(MediaType.IMAGE_PNG, pictureResponse.getHeaders().getContentType());
        var pictureOutput = new ByteArrayOutputStream();
        pictureResponse.getBody().writeTo(pictureOutput);
        assertArrayEquals(picture, pictureOutput.toByteArray());

        var profile = mockMvc.perform(get("/trainers/profile").session(trainerSession))
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(profile.getResponse().getContentAsString().contains("width: 200px; height: 200px"));

        MockHttpSession noPictureSession = register(
                "/provider/register",
                "placeholder-" + suffix + "@example.com",
                "Placeholder Trainer",
                "StrongPassword1",
                "trainer");
        Long noPictureTrainerId = (Long) noPictureSession.getAttribute("userId");
        ResponseEntity<StreamingResponseBody> placeholderResponse =
                trainerUiController.profilePicture(noPictureTrainerId);
        assertEquals(
                MediaType.IMAGE_JPEG, placeholderResponse.getHeaders().getContentType());
        var placeholderOutput = new ByteArrayOutputStream();
        placeholderResponse.getBody().writeTo(placeholderOutput);
        assertArrayEquals(
                new org.springframework.core.io.ClassPathResource("static/images/trainer-default.jpg")
                        .getContentAsByteArray(),
                placeholderOutput.toByteArray());
    }

    private MockHttpSession register(
            String path, String email, String name, String password, String role) throws Exception {
        MockHttpServletRequestBuilder request = post(path)
                .param("email", email)
                .param("name", name)
                .param("password", password);
        if ("customer".equals(role)) {
            request.param("currentWeight", "180")
                    .param("goalWeight", "160")
                    .param("fitnessLevel", "Beginner")
                    .param("fitnessGoals", "Build strength")
                    .param("injuriesOrHealthConcerns", "")
                    .param("phoneNumber", "555-0101");
        } else {
            request.param("yearsOfExperience", "4")
                    .param("specialties", "Strength")
                    .param("biography", "Demo trainer")
                    .param("certifications", "CPT");
        }

        var result = mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andReturn();
        HttpSession session = result.getRequest().getSession(false);
        if (!(session instanceof MockHttpSession mockSession)) {
            throw new AssertionError("Registration did not create a session.");
        }
        return mockSession;
    }

    private void page(String path, MockHttpSession session) throws Exception {
        MockHttpServletRequestBuilder request = get(path);
        if (session != null) {
            request.session(session);
        }
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
    }
}
