package com.ai.balancelab_be.domain.healthPrediction.controller;

import com.ai.balancelab_be.domain.healthPrediction.dto.HealthPredictionRequest;
import com.ai.balancelab_be.domain.healthPrediction.dto.PredictionSaveDto;
import com.ai.balancelab_be.domain.healthPrediction.service.HealthPredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/health-prediction")
public class HealthPredictionController {

    private final HealthPredictionService healthPredictionService;

    @PostMapping("/predict")
    public ResponseEntity<String> predictHealth(@RequestBody HealthPredictionRequest dto) {
        return healthPredictionService.predictHealth(dto);
    }

    @PostMapping("/savePrediction")
    public ResponseEntity<String> savePrediction(@RequestBody PredictionSaveDto dto) {
        String savePrediction=healthPredictionService.savePredictionRecord(
                dto
        );
        return ResponseEntity.ok(savePrediction);
    }

}
