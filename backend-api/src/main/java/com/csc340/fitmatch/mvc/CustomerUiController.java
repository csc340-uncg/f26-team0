package com.csc340.fitmatch.mvc;

import com.csc340.fitmatch.dto.BookSessionRequest;
import com.csc340.fitmatch.dto.CustomerUpdateRequest;
import com.csc340.fitmatch.dto.RescheduleSessionRequest;
import com.csc340.fitmatch.dto.ReviewRequest;
import com.csc340.fitmatch.dto.TrainerResponse;
import com.csc340.fitmatch.dto.TrainingServiceResponse;
import com.csc340.fitmatch.dto.TrainingSessionResponse;
import com.csc340.fitmatch.service.CustomerService;
import com.csc340.fitmatch.service.NotFoundException;
import com.csc340.fitmatch.service.ReviewService;
import com.csc340.fitmatch.service.TimeslotService;
import com.csc340.fitmatch.service.TrainerProfileService;
import com.csc340.fitmatch.service.TrainingCatalogService;
import com.csc340.fitmatch.service.TrainingSessionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/customers")
public class CustomerUiController {
    private final CustomerService customerService;
    private final TrainerProfileService trainerProfileService;
    private final TrainingCatalogService trainingCatalogService;
    private final TrainingSessionService trainingSessionService;
    private final TimeslotService timeslotService;
    private final ReviewService reviewService;

    public CustomerUiController(
            CustomerService customerService,
            TrainerProfileService trainerProfileService,
            TrainingCatalogService trainingCatalogService,
            TrainingSessionService trainingSessionService,
            TimeslotService timeslotService,
            ReviewService reviewService) {
        this.customerService = customerService;
        this.trainerProfileService = trainerProfileService;
        this.trainingCatalogService = trainingCatalogService;
        this.trainingSessionService = trainingSessionService;
        this.timeslotService = timeslotService;
        this.reviewService = reviewService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long customerId = UiSession.requireId(session, "customer");
        model.addAttribute("customer", customerService.getProfile(customerId));
        model.addAttribute("sessions", trainingSessionService.listForCustomer(customerId));
        return "customer/dashboard";
    }

    @GetMapping("/browse-trainers")
    public String browseTrainers(@RequestParam(required = false) String category, Model model) {
        model.addAttribute("trainers", trainerCards(category));
        model.addAttribute("category", category == null ? "" : category);
        return "customer/browse-trainers";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        model.addAttribute("customer",
                customerService.getProfile(UiSession.requireId(session, "customer")));
        return "customer/profile";
    }

    @GetMapping("/edit-details")
    public String editDetails(HttpSession session, Model model) {
        model.addAttribute("customer",
                customerService.getProfile(UiSession.requireId(session, "customer")));
        return "customer/edit-details";
    }

    @PostMapping("/edit-details")
    public String updateDetails(@Valid CustomerUpdateRequest request, HttpSession session) {
        customerService.updateProfile(UiSession.requireId(session, "customer"), request);
        return "redirect:/customers/profile";
    }

    @GetMapping("/my-sessions")
    public String mySessions(
            @RequestParam(defaultValue = "upcoming") String status,
            HttpSession session,
            Model model) {
        Long customerId = UiSession.requireId(session, "customer");
        model.addAttribute("sessions", trainingSessionService.listForCustomer(customerId));
        model.addAttribute("reviews", reviewService.listForCustomer(customerId));
        model.addAttribute("status", status);
        return "customer/my-sessions";
    }

    @GetMapping("/sessions/{sessionId}")
    public String sessionDetails(
            @PathVariable Long sessionId,
            HttpSession session,
            Model model) {
        TrainingSessionResponse trainingSession = findCustomerSession(
                UiSession.requireId(session, "customer"), sessionId);
        model.addAttribute("trainingSession", trainingSession);
        model.addAttribute("timeslots", timeslotService.list(trainingSession.trainerId(), true));
        return "customer/session-details";
    }

    @PostMapping("/sessions/{sessionId}/cancel")
    public String cancelSession(@PathVariable Long sessionId, HttpSession session) {
        trainingSessionService.cancel(UiSession.requireId(session, "customer"), sessionId);
        return "redirect:/customers/my-sessions";
    }

    @PostMapping("/sessions/{sessionId}/reschedule")
    public String rescheduleSession(
            @PathVariable Long sessionId,
            @Valid RescheduleSessionRequest request,
            HttpSession session) {
        trainingSessionService.reschedule(
                UiSession.requireId(session, "customer"), sessionId, request);
        return "redirect:/customers/my-sessions";
    }

    @GetMapping("/sessions/{sessionId}/review")
    public String reviewForm(
            @PathVariable Long sessionId,
            HttpSession session,
            Model model) {
        model.addAttribute("trainingSession",
                findCustomerSession(UiSession.requireId(session, "customer"), sessionId));
        return "customer/review";
    }

    @PostMapping("/sessions/{sessionId}/review")
    public String submitReview(
            @PathVariable Long sessionId,
            @Valid ReviewRequest request,
            HttpSession session) {
        reviewService.create(UiSession.requireId(session, "customer"), sessionId, request);
        return "redirect:/customers/my-sessions?status=completed";
    }

    @GetMapping("/trainers/{trainerId}")
    public String trainerProfile(@PathVariable Long trainerId, Model model) {
        TrainerResponse trainer = trainerProfileService.getProfile(trainerId);
        model.addAttribute("trainer", trainer);
        model.addAttribute("services", trainingCatalogService.listPublishedForTrainer(trainerId));
        model.addAttribute("timeslots", timeslotService.list(trainerId, true));
        model.addAttribute("reviews", reviewService.listForTrainer(trainerId));
        return "customer/trainer-profile";
    }

    @PostMapping("/book")
    public String bookSession(@Valid BookSessionRequest request, HttpSession session) {
        trainingSessionService.book(UiSession.requireId(session, "customer"), request);
        return "redirect:/customers/my-sessions";
    }

    private TrainingSessionResponse findCustomerSession(Long customerId, Long sessionId) {
        return trainingSessionService.listForCustomer(customerId).stream()
                .filter(trainingSession -> trainingSession.id().equals(sessionId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        "Training session " + sessionId + " was not found."));
    }

    private List<TrainerCard> trainerCards(String category) {
        List<TrainingServiceResponse> services = trainingCatalogService.listPublished();
        return trainerProfileService.browse(category).stream()
                .map(trainer -> new TrainerCard(trainer, services.stream()
                        .filter(service -> service.trainerId().equals(trainer.id()))
                        .toList()))
                .toList();
    }
}
