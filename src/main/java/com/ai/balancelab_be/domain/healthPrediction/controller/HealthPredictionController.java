package com.ai.balancelab_be.domain.healthPrediction.controller;

import com.ai.balancelab_be.domain.healthPrediction.service.HealthPredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/health-prediction")
public class HealthPredictionController {

    private final HealthPredictionService healthPredictionService;

    @PostMapping("/predict")
    public ResponseEntity<String> predictHealth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long memberId = Long.parseLong(authentication.getName());
        
        String predictionResult = healthPredictionService.predictHealth(memberId);
        return ResponseEntity.ok(predictionResult);
    }
}
