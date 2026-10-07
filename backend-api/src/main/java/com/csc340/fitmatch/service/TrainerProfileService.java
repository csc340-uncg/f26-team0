package com.csc340.fitmatch.service;

import com.csc340.fitmatch.dto.TrainerRegistrationRequest;
import com.csc340.fitmatch.dto.TrainerResponse;
import com.csc340.fitmatch.dto.TrainerUpdateRequest;
import com.csc340.fitmatch.entity.Trainer;
import com.csc340.fitmatch.repository.TrainerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Transactional
public class TrainerProfileService {
    private final TrainerRepository trainerRepository;
    private final PasswordEncoder passwordEncoder;

    public TrainerProfileService(TrainerRepository trainerRepository, PasswordEncoder passwordEncoder) {
        this.trainerRepository = trainerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public TrainerResponse register(TrainerRegistrationRequest request) {
        ensureEmailAvailable(request.email());
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BadRequestException("Password must be no more than 72 UTF-8 bytes.");
        }
        Trainer trainer = new Trainer();
        trainer.setName(request.name().trim());
        trainer.setEmail(request.email().trim().toLowerCase(java.util.Locale.ROOT));
        trainer.setPassword(passwordEncoder.encode(request.password()));
        trainer.setAccountStatus("ACTIVE");
        trainer.setBiography(request.biography());
        trainer.setCertifications(request.certifications());
        trainer.setYearsOfExperience(request.yearsOfExperience());
        trainer.setSpecialties(request.specialties());
        return TrainerResponse.from(trainerRepository.save(trainer));
    }

    @Transactional(readOnly = true)
    public TrainerResponse getProfile(Long trainerId) {
        return TrainerResponse.from(findTrainer(trainerId));
    }

    @Transactional(readOnly = true)
    public List<TrainerResponse> browse(String category) {
        List<Trainer> trainers = category == null || category.isBlank()
                ? trainerRepository.findAll()
                : trainerRepository.findMatchingCategory(category.trim());
        return trainers.stream().map(TrainerResponse::from).toList();
    }

    public TrainerResponse updateProfile(Long trainerId, TrainerUpdateRequest request) {
        Trainer trainer = findTrainer(trainerId);
        if (!trainer.getEmail().equalsIgnoreCase(request.email())
                && trainerRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("An account with that email already exists.");
        }
        trainer.setName(request.name().trim());
        trainer.setEmail(request.email().trim().toLowerCase(java.util.Locale.ROOT));
        trainer.setBiography(request.biography());
        trainer.setCertifications(request.certifications());
        trainer.setYearsOfExperience(request.yearsOfExperience());
        trainer.setSpecialties(request.specialties());
        return TrainerResponse.from(trainerRepository.save(trainer));
    }

    Trainer findTrainer(Long trainerId) {
        return trainerRepository.findById(trainerId)
                .orElseThrow(() -> new NotFoundException("Trainer " + trainerId + " was not found."));
    }

    private void ensureEmailAvailable(String email) {
        if (trainerRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("An account with that email already exists.");
        }
    }
}
