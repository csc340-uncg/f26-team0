package com.csc340.fitmatch.repository;

import com.csc340.fitmatch.entity.Trainer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    boolean existsByEmailIgnoreCase(String email);

    List<Trainer> findBySpecialtiesContainingIgnoreCase(String category);

    @Query("""
            select distinct trainer
            from Trainer trainer
            left join trainer.trainingServices trainingService
            where lower(coalesce(trainer.specialties, '')) like lower(concat('%', :category, '%'))
               or (lower(trainingService.category) = lower(:category)
                   and upper(trainingService.status) = 'PUBLISHED')
            """)
    List<Trainer> findMatchingCategory(@Param("category") String category);
}
