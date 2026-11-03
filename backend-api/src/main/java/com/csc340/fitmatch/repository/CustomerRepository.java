package com.csc340.fitmatch.repository;

import com.csc340.fitmatch.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmailIgnoreCase(String email);

    Optional<Customer> findByEmailIgnoreCase(String email);
}
