package com.csc340.fitmatch.mvc;

import com.csc340.fitmatch.dto.CustomerRegistrationRequest;
import com.csc340.fitmatch.dto.CustomerResponse;
import com.csc340.fitmatch.dto.TrainerRegistrationRequest;
import com.csc340.fitmatch.dto.TrainerResponse;
import com.csc340.fitmatch.service.BadRequestException;
import com.csc340.fitmatch.service.CustomerService;
import com.csc340.fitmatch.service.TrainerProfileService;
import java.io.IOException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AppController {
    private final CustomerService customerService;
    private final TrainerProfileService trainerProfileService;

    public AppController(CustomerService customerService, TrainerProfileService trainerProfileService) {
        this.customerService = customerService;
        this.trainerProfileService = trainerProfileService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("trainers", trainerProfileService.browse(null));
        return "public/index";
    }

    @GetMapping("/login")
    public String login() {
        return "public/login";
    }

    @PostMapping("/login")
    public String authenticate(
            @RequestParam String role,
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session) {
        if ("trainer".equalsIgnoreCase(role)) {
            TrainerResponse trainer = trainerProfileService.authenticate(email, password);
            session.setAttribute("userId", trainer.id());
            session.setAttribute("userRole", "trainer");
            session.setAttribute("userName", trainer.name());
            return "redirect:/trainers/dashboard";
        }
        if (!"customer".equalsIgnoreCase(role)) {
            throw new BadRequestException("Choose a valid account type.");
        }
        CustomerResponse customer = customerService.authenticate(email, password);
        session.setAttribute("userId", customer.id());
        session.setAttribute("userRole", "customer");
        session.setAttribute("userName", customer.name());
        return "redirect:/customers/dashboard";
    }

    @GetMapping("/register")
    public String registerCustomer() {
        return "public/register";
    }

    @PostMapping("/register")
    public String createCustomer(@Valid CustomerRegistrationRequest request, HttpSession session) {
        CustomerResponse customer = customerService.register(request);
        setSession(session, customer.id(), "customer", customer.name());
        return "redirect:/customers/dashboard";
    }

    @GetMapping("/provider/register")
    public String registerTrainer() {
        return "trainer/register";
    }

    @PostMapping("/provider/register")
    public String createTrainer(
            @Valid TrainerRegistrationRequest request,
            @RequestParam(name = "profilePicture", required = false) MultipartFile profilePicture,
            HttpSession session) {
        TrainerResponse trainer = trainerProfileService.register(request);
        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                trainerProfileService.updateProfilePicture(
                        trainer.id(), profilePicture.getBytes(), profilePicture.getContentType());
            } catch (IOException exception) {
                throw new BadRequestException("The profile picture could not be read.");
            }
        }
        setSession(session, trainer.id(), "trainer", trainer.name());
        return "redirect:/trainers/dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    private void setSession(HttpSession session, Long id, String role, String name) {
        session.setAttribute("userId", id);
        session.setAttribute("userRole", role);
        session.setAttribute("userName", name);
    }
}
