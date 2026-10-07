package com.csc340.fitmatch.dto;

import com.csc340.fitmatch.entity.Customer;

import java.math.BigDecimal;

public record CustomerResponse(
        Long id,
        String name,
        String email,
        String phoneNumber,
        String accountStatus,
        BigDecimal currentWeight,
        BigDecimal goalWeight,
        String fitnessLevel,
        String fitnessGoals,
        String injuriesOrHealthConcerns) {

    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getName(), customer.getEmail(),
                customer.getPhoneNumber(), customer.getAccountStatus(), customer.getCurrentWeight(),
                customer.getGoalWeight(), customer.getFitnessLevel(), customer.getFitnessGoals(),
                customer.getInjuriesOrHealthConcerns());
    }
}
