package com.csc340.fitmatch.service;

import com.csc340.fitmatch.dto.CustomerRegistrationRequest;
import com.csc340.fitmatch.dto.CustomerResponse;
import com.csc340.fitmatch.dto.CustomerUpdateRequest;
import com.csc340.fitmatch.entity.Customer;
import com.csc340.fitmatch.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
@Transactional
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CustomerResponse register(CustomerRegistrationRequest request) {
        ensureEmailAvailable(request.email());
        validatePasswordLength(request.password());
        Customer customer = new Customer();
        customer.setName(request.name().trim());
        customer.setEmail(request.email().trim().toLowerCase(Locale.ROOT));
        customer.setPassword(passwordEncoder.encode(request.password()));
        customer.setAccountStatus("ACTIVE");
        customer.setPhoneNumber(request.phoneNumber());
        customer.setCurrentWeight(request.currentWeight());
        customer.setGoalWeight(request.goalWeight());
        customer.setFitnessLevel(request.fitnessLevel());
        customer.setFitnessGoals(request.fitnessGoals());
        customer.setInjuriesOrHealthConcerns(request.injuriesOrHealthConcerns());
        return CustomerResponse.from(customerRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public CustomerResponse getProfile(Long customerId) {
        return CustomerResponse.from(findCustomer(customerId));
    }

    public CustomerResponse updateProfile(Long customerId, CustomerUpdateRequest request) {
        Customer customer = findCustomer(customerId);
        if (!customer.getEmail().equalsIgnoreCase(request.email())
                && customerRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("An account with that email already exists.");
        }
        customer.setName(request.name().trim());
        customer.setEmail(request.email().trim().toLowerCase(Locale.ROOT));
        customer.setPhoneNumber(request.phoneNumber());
        customer.setCurrentWeight(request.currentWeight());
        customer.setGoalWeight(request.goalWeight());
        customer.setFitnessLevel(request.fitnessLevel());
        customer.setFitnessGoals(request.fitnessGoals());
        customer.setInjuriesOrHealthConcerns(request.injuriesOrHealthConcerns());
        return CustomerResponse.from(customerRepository.save(customer));
    }

    private Customer findCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer " + customerId + " was not found."));
    }

    private void ensureEmailAvailable(String email) {
        if (customerRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("An account with that email already exists.");
        }
    }

    private void validatePasswordLength(String password) {
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BadRequestException("Password must be no more than 72 UTF-8 bytes.");
        }
    }
}
