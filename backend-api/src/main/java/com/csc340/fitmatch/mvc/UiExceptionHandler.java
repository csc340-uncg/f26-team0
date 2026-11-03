package com.csc340.fitmatch.mvc;

import com.csc340.fitmatch.service.BadRequestException;
import com.csc340.fitmatch.service.ConflictException;
import com.csc340.fitmatch.service.NotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = {AppController.class, CustomerUiController.class, TrainerUiController.class})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UiExceptionHandler {
    @ModelAttribute
    public void addSessionUser(HttpSession session, Model model) {
        Object role = session.getAttribute("userRole");
        Object name = session.getAttribute("userName");
        if (role != null) {
            model.addAttribute("userRole", role);
        }
        if (name != null) {
            model.addAttribute("userName", name);
        }
    }

    @ExceptionHandler({BadRequestException.class, ConflictException.class, NotFoundException.class})
    public String handleServiceError(
            RuntimeException exception, Model model, HttpServletResponse response) {
        model.addAttribute("errorMessage", exception.getMessage());
        if (exception instanceof NotFoundException) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
        } else if (exception instanceof ConflictException) {
            response.setStatus(HttpStatus.CONFLICT.value());
        } else {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return "shared/error";
    }

    @ExceptionHandler(BindException.class)
    public String handleValidationError(
            BindException exception, Model model, HttpServletResponse response) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Please check the information you entered.");
        model.addAttribute("errorMessage", message);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return "shared/error";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataConflict(Model model, HttpServletResponse response) {
        model.addAttribute("errorMessage", "That change conflicts with existing data.");
        response.setStatus(HttpStatus.CONFLICT.value());
        return "shared/error";
    }
}
