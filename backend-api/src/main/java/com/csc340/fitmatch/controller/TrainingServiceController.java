package com.csc340.fitmatch.controller;

import com.csc340.fitmatch.dto.TrainingServiceResponse;
import com.csc340.fitmatch.service.TrainingCatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class TrainingServiceController {
    private final TrainingCatalogService catalogService;

    public TrainingServiceController(TrainingCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public List<TrainingServiceResponse> browse(@RequestParam String category) {
        return catalogService.listPublishedByCategory(category.trim());
    }
}
