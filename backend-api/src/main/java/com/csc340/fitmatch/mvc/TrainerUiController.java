package com.csc340.fitmatch.mvc;

import com.csc340.fitmatch.dto.ReviewReplyRequest;
import com.csc340.fitmatch.dto.SessionNotesRequest;
import com.csc340.fitmatch.dto.TimeslotRequest;
import com.csc340.fitmatch.dto.TrainerResponse;
import com.csc340.fitmatch.dto.TrainerStatisticsResponse;
import com.csc340.fitmatch.dto.TrainerUpdateRequest;
import com.csc340.fitmatch.dto.TrainingServiceRequest;
import com.csc340.fitmatch.dto.TrainingServiceResponse;
import com.csc340.fitmatch.dto.TrainingSessionResponse;
import com.csc340.fitmatch.service.CustomerService;
import com.csc340.fitmatch.service.BadRequestException;
import com.csc340.fitmatch.service.NotFoundException;
import com.csc340.fitmatch.service.ReviewService;
import com.csc340.fitmatch.service.TimeslotService;
import com.csc340.fitmatch.service.TrainerProfileService;
import com.csc340.fitmatch.service.TrainerStatisticsService;
import com.csc340.fitmatch.service.TrainingCatalogService;
import com.csc340.fitmatch.service.TrainingSessionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/trainers")
public class TrainerUiController {
    private final TrainerProfileService trainerProfileService;
    private final CustomerService customerService;
    private final TrainingCatalogService trainingCatalogService;
    private final TrainingSessionService trainingSessionService;
    private final TimeslotService timeslotService;
    private final TrainerStatisticsService trainerStatisticsService;
    private final ReviewService reviewService;

    public TrainerUiController(
            TrainerProfileService trainerProfileService,
            CustomerService customerService,
            TrainingCatalogService trainingCatalogService,
            TrainingSessionService trainingSessionService,
            TimeslotService timeslotService,
            TrainerStatisticsService trainerStatisticsService,
            ReviewService reviewService) {
        this.trainerProfileService = trainerProfileService;
        this.customerService = customerService;
        this.trainingCatalogService = trainingCatalogService;
        this.trainingSessionService = trainingSessionService;
        this.timeslotService = timeslotService;
        this.trainerStatisticsService = trainerStatisticsService;
        this.reviewService = reviewService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long trainerId = UiSession.requireId(session, "trainer");
        addDashboardData(trainerId, model);
        return "trainer/dashboard";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Long trainerId = UiSession.requireId(session, "trainer");
        TrainerResponse trainer = trainerProfileService.getProfile(trainerId);
        model.addAttribute("trainer", trainer);
        model.addAttribute("statistics", trainerStatisticsService.getStatistics(trainerId));
        return "trainer/profile";
    }

    @GetMapping("/{trainerId}/picture")
    public ResponseEntity<StreamingResponseBody> profilePicture(@PathVariable Long trainerId) {
        MediaType contentType = MediaType.parseMediaType(
                trainerProfileService.getProfilePictureContentType(trainerId));
        StreamingResponseBody stream = output ->
                trainerProfileService.writeProfilePicture(trainerId, output);
        return ResponseEntity.ok()
                .contentType(contentType)
                .body(stream);
    }

    @GetMapping("/edit-details")
    public String editDetails(HttpSession session, Model model) {
        model.addAttribute("trainer",
                trainerProfileService.getProfile(UiSession.requireId(session, "trainer")));
        return "trainer/edit-details";
    }

    @PostMapping("/edit-details")
    public String updateDetails(
            @Valid TrainerUpdateRequest request,
            @RequestParam(name = "profilePicture", required = false) MultipartFile profilePicture,
            HttpSession session) {
        Long trainerId = UiSession.requireId(session, "trainer");
        trainerProfileService.updateProfile(trainerId, request);
        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                trainerProfileService.updateProfilePicture(
                        trainerId, profilePicture.getBytes(), profilePicture.getContentType());
            } catch (IOException exception) {
                throw new BadRequestException("The profile picture could not be read.");
            }
        }
        return "redirect:/trainers/profile";
    }

    @GetMapping("/manage-services")
    public String manageServices(HttpSession session, Model model) {
        Long trainerId = UiSession.requireId(session, "trainer");
        model.addAttribute("trainer", trainerProfileService.getProfile(trainerId));
        model.addAttribute("services", trainingCatalogService.listForTrainer(trainerId));
        model.addAttribute("timeslots", timeslotService.list(trainerId, false));
        return "trainer/manage-services";
    }

    @GetMapping("/new-service")
    public String newService(HttpSession session, Model model) {
        model.addAttribute("trainer",
                trainerProfileService.getProfile(UiSession.requireId(session, "trainer")));
        return "trainer/new-service";
    }

    @PostMapping("/services")
    public String createService(@Valid TrainingServiceRequest request, HttpSession session) {
        trainingCatalogService.create(UiSession.requireId(session, "trainer"), request);
        return "redirect:/trainers/manage-services";
    }

    @GetMapping("/edit-service/{serviceId}")
    public String editService(
            @PathVariable Long serviceId,
            HttpSession session,
            Model model) {
        Long trainerId = UiSession.requireId(session, "trainer");
        TrainingServiceResponse service = trainingCatalogService.listForTrainer(trainerId).stream()
                .filter(item -> item.id().equals(serviceId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        "Training service " + serviceId + " was not found."));
        model.addAttribute("service", service);
        return "trainer/edit-service";
    }

    @PostMapping("/edit-service/{serviceId}")
    public String updateService(
            @PathVariable Long serviceId,
            @Valid TrainingServiceRequest request,
            HttpSession session) {
        trainingCatalogService.update(
                UiSession.requireId(session, "trainer"), serviceId, request);
        return "redirect:/trainers/manage-services";
    }

    @PostMapping("/timeslots")
    public String createTimeslot(@Valid TimeslotRequest request, HttpSession session) {
        timeslotService.create(UiSession.requireId(session, "trainer"), request);
        return "redirect:/trainers/manage-services";
    }

    @PostMapping("/timeslots/{timeslotId}/delete")
    public String deleteTimeslot(@PathVariable Long timeslotId, HttpSession session) {
        timeslotService.delete(UiSession.requireId(session, "trainer"), timeslotId);
        return "redirect:/trainers/manage-services";
    }

    @GetMapping("/view-clients")
    public String viewClients(HttpSession session, Model model) {
        Long trainerId = UiSession.requireId(session, "trainer");
        model.addAttribute("customers", trainerStatisticsService.getStatistics(trainerId).customers());
        return "trainer/view-clients";
    }

    @GetMapping("/clients/{customerId}")
    public String clientProfile(
            @PathVariable Long customerId,
            HttpSession session,
            Model model) {
        Long trainerId = UiSession.requireId(session, "trainer");
        List<TrainingSessionResponse> sessions = trainingSessionService.listForTrainer(trainerId).stream()
                .filter(trainingSession -> trainingSession.customerId().equals(customerId))
                .toList();
        if (sessions.isEmpty()) {
            throw new NotFoundException(
                    "Customer " + customerId + " has no sessions with this trainer.");
        }
        model.addAttribute("customer", customerService.getProfile(customerId));
        model.addAttribute("sessions", sessions);
        return "trainer/client-profile";
    }

    @PostMapping("/sessions/{sessionId}/notes")
    public String updateNotes(
            @PathVariable Long sessionId,
            @Valid SessionNotesRequest request,
            HttpSession session) {
        trainingSessionService.updateNotesForTrainer(
                UiSession.requireId(session, "trainer"), sessionId, request);
        return "redirect:/trainers/dashboard";
    }

    @PostMapping("/reviews/{reviewId}/reply")
    public String replyToReview(
            @PathVariable Long reviewId,
            @Valid ReviewReplyRequest request,
            HttpSession session) {
        reviewService.reply(UiSession.requireId(session, "trainer"), reviewId, request);
        return "redirect:/trainers/dashboard";
    }

    @GetMapping("/provider-dashboard")
    public String providerDashboard(HttpSession session, Model model) {
        Long trainerId = UiSession.requireId(session, "trainer");
        addDashboardData(trainerId, model);
        return "trainer/provider-dashboard";
    }

    private void addDashboardData(Long trainerId, Model model) {
        TrainerResponse trainer = trainerProfileService.getProfile(trainerId);
        TrainerStatisticsResponse statistics = trainerStatisticsService.getStatistics(trainerId);
        model.addAttribute("trainer", trainer);
        model.addAttribute("statistics", statistics);
        model.addAttribute("sessions", trainingSessionService.listForTrainer(trainerId));
        model.addAttribute("reviews", reviewService.listForTrainer(trainerId));
        model.addAttribute("services", trainingCatalogService.listForTrainer(trainerId));
    }
}
