package com.ai.balancelab_be.domain.dietAnalysis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import com.ai.balancelab_be.domain.dietAnalysis.service.DietAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diet-analysis")
public class DietAnalysisController {

    private final DietAnalysisService dietAnalysisService;

    @PostMapping("/analyze")
    public ResponseEntity<String> analyzeDiet(@RequestBody String userId) {
        dietAnalysisService.analyzeDiet(userId);
        return ResponseEntity.ok("Diet analysis completed");
    }
}
