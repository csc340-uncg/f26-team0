package com.csc340.fitmatch.service;

import com.csc340.fitmatch.dto.TrainerRegistrationRequest;
import com.csc340.fitmatch.dto.TrainerResponse;
import com.csc340.fitmatch.dto.TrainerUpdateRequest;
import com.csc340.fitmatch.entity.Trainer;
import com.csc340.fitmatch.repository.TrainerRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import javax.sql.rowset.serial.SerialBlob;

@Service
@Transactional
public class TrainerProfileService {
    private static final String DEFAULT_PICTURE_CONTENT_TYPE = "image/jpeg";

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
    public TrainerResponse authenticate(String email, String password) {
        Trainer trainer = trainerRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new BadRequestException("Invalid email or password."));
        if (!passwordEncoder.matches(password, trainer.getPassword())) {
            throw new BadRequestException("Invalid email or password.");
        }
        return TrainerResponse.from(trainer);
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

    public void updateProfilePicture(Long trainerId, byte[] picture, String contentType) {
        Trainer trainer = findTrainer(trainerId);
        try {
            trainer.setProfilePicture(picture == null || picture.length == 0
                    ? null
                    : new SerialBlob(picture));
        } catch (SQLException exception) {
            throw new BadRequestException("The profile picture could not be saved.");
        }
        trainer.setProfilePictureContentType(
                picture == null || picture.length == 0 ? null : normalizePictureContentType(contentType));
        trainerRepository.save(trainer);
    }

    @Transactional(readOnly = true)
    public String getProfilePictureContentType(Long trainerId) {
        Trainer trainer = findTrainer(trainerId);
        return trainer.getProfilePicture() == null
                ? DEFAULT_PICTURE_CONTENT_TYPE
                : normalizePictureContentType(trainer.getProfilePictureContentType());
    }

    @Transactional(readOnly = true)
    public void writeProfilePicture(Long trainerId, OutputStream output) throws IOException {
        Trainer trainer = findTrainer(trainerId);
        if (trainer.getProfilePicture() == null) {
            try (var input = new ClassPathResource("static/images/trainer-default.jpg").getInputStream()) {
                input.transferTo(output);
            }
            return;
        }
        try (var input = trainer.getProfilePicture().getBinaryStream()) {
            input.transferTo(output);
        } catch (SQLException exception) {
            throw new IOException("Unable to read trainer profile picture.", exception);
        }
    }

    private String normalizePictureContentType(String contentType) {
        if (contentType == null) {
            return DEFAULT_PICTURE_CONTENT_TYPE;
        }
        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/png", "image/gif", "image/webp" -> contentType.toLowerCase(Locale.ROOT);
            default -> DEFAULT_PICTURE_CONTENT_TYPE;
        };
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
