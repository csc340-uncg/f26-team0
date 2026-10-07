package com.csc340.fitmatch.repository;

import com.csc340.fitmatch.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByTrainerIdOrderByIdDesc(Long trainerId);
}
