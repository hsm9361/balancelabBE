package com.ai.balancelab_be.domain.dietAnalysis.controller;

import com.ai.balancelab_be.domain.dietAnalysis.dto.DietAnalysisRequest;
import com.ai.balancelab_be.domain.dietAnalysis.dto.DietAnalysisResponse;
import com.ai.balancelab_be.domain.dietAnalysis.dto.FoodNutrition;
import com.ai.balancelab_be.domain.dietAnalysis.dto.Nutrition;
import com.ai.balancelab_be.domain.dietAnalysis.service.DietAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diet-analysis")
public class DietAnalysisController {

    private final DietAnalysisService dietAnalysisService;

    @PostMapping(value = "/message")
    public ResponseEntity<DietAnalysisResponse> receiveDietMessage(
            @RequestParam("message") String message) {
        try {
            System.out.println("컨트롤러 메세지1(컨트롤러에 들어온값): " + message);
            if (message == null || message.trim().isEmpty()) {
                System.out.println("컨트롤러 메세지1: 메시지가 비어 있습니다.");
                return ResponseEntity.badRequest().body(
                        new DietAnalysisResponse(
                                Collections.emptyList(),
                                Collections.emptyList(),
                                new Nutrition(0, 0, 0, 0, 0, 0, 0),
                                Collections.emptyList(),
                                Collections.emptyList()
                        )
                );
            }

            DietAnalysisRequest dietAnalysisRequest = new DietAnalysisRequest(message);
            DietAnalysisResponse response = dietAnalysisService.getFoodNameResponse(dietAnalysisRequest);
            System.out.println("컨트롤러 메세지2(서비스에서 넘어온 전체값): " + response);
            System.out.println("컨트롤러 메세지2(foodList): " + response.getFoodList());
            System.out.println("컨트롤러 메세지2(nutritionPerFood): " + response.getNutritionPerFood());
            System.out.println("컨트롤러 메세지2(totalNutrition): " + response.getTotalNutrition());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("컨트롤러 메세지2: 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                    new DietAnalysisResponse(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            new Nutrition(0, 0, 0, 0, 0, 0, 0),
                            Collections.emptyList(),
                            Collections.emptyList()
                    )
            );
        }
    }
}