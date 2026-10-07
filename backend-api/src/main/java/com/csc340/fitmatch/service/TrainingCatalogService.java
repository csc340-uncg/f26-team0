package com.csc340.fitmatch.service;

import com.csc340.fitmatch.dto.TrainingServiceRequest;
import com.csc340.fitmatch.dto.TrainingServiceResponse;
import com.csc340.fitmatch.entity.Trainer;
import com.csc340.fitmatch.entity.TrainingService;
import com.csc340.fitmatch.repository.TrainingServiceRepository;
import com.csc340.fitmatch.repository.TrainerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional
public class TrainingCatalogService {
    private static final Set<String> SERVICE_STATUSES = Set.of("DRAFT", "PUBLISHED", "ARCHIVED");

    private final TrainingServiceRepository trainingServiceRepository;
    private final TrainerRepository trainerRepository;

    public TrainingCatalogService(
            TrainingServiceRepository trainingServiceRepository,
            TrainerRepository trainerRepository) {
        this.trainingServiceRepository = trainingServiceRepository;
        this.trainerRepository = trainerRepository;
    }

    public TrainingServiceResponse create(Long trainerId, TrainingServiceRequest request) {
        Trainer trainer = findTrainer(trainerId);
        TrainingService trainingService = new TrainingService();
        trainingService.setTrainer(trainer);
        apply(trainingService, request, "PUBLISHED");
        return TrainingServiceResponse.from(trainingServiceRepository.save(trainingService));
    }

    @Transactional(readOnly = true)
    public List<TrainingServiceResponse> listPublishedForTrainer(Long trainerId) {
        ensureTrainerExists(trainerId);
        return trainingServiceRepository
                .findAllByTrainerIdAndStatusIgnoreCaseOrderById(trainerId, "PUBLISHED")
                .stream().map(TrainingServiceResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<TrainingServiceResponse> listPublishedByCategory(String category) {
        return trainingServiceRepository
                .findAllByCategoryIgnoreCaseAndStatusIgnoreCaseOrderById(category, "PUBLISHED")
                .stream().map(TrainingServiceResponse::from).toList();
    }

    public TrainingServiceResponse update(
            Long trainerId, Long serviceId, TrainingServiceRequest request) {
        TrainingService trainingService = findService(serviceId);
        if (!trainingService.getTrainer().getId().equals(trainerId)) {
            throw new NotFoundException("Training service " + serviceId + " was not found for trainer "
                    + trainerId + ".");
        }
        apply(trainingService, request, trainingService.getStatus());
        return TrainingServiceResponse.from(trainingServiceRepository.save(trainingService));
    }

    private void apply(
            TrainingService trainingService, TrainingServiceRequest request, String existingStatus) {
        trainingService.setName(request.name().trim());
        trainingService.setDescription(request.description());
        trainingService.setPrice(request.price());
        trainingService.setCategory(request.category().trim());
        String status = request.status() == null || request.status().isBlank()
                ? existingStatus
                : request.status();
        validateStatus(status);
        trainingService.setStatus(status.trim().toUpperCase(Locale.ROOT));
    }

    private void validateStatus(String status) {
        if (!SERVICE_STATUSES.contains(status.trim().toUpperCase(Locale.ROOT))) {
            throw new BadRequestException("Service status must be DRAFT, PUBLISHED, or ARCHIVED.");
        }
    }

    private TrainingService findService(Long serviceId) {
        return trainingServiceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException(
                        "Training service " + serviceId + " was not found."));
    }

    private Trainer findTrainer(Long trainerId) {
        return trainerRepository.findById(trainerId)
                .orElseThrow(() -> new NotFoundException("Trainer " + trainerId + " was not found."));
    }

    private void ensureTrainerExists(Long trainerId) {
        if (!trainerRepository.existsById(trainerId)) {
            throw new NotFoundException("Trainer " + trainerId + " was not found.");
        }
    }
}
