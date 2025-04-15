package com.ai.balancelab_be.domain.dietAnalysis.controller;

import com.ai.balancelab_be.domain.dietAnalysis.dto.DietAnalysisRequest;
import com.ai.balancelab_be.domain.dietAnalysis.dto.DietAnalysisResponse;
import com.ai.balancelab_be.domain.dietAnalysis.dto.Nutrition;
import com.ai.balancelab_be.domain.dietAnalysis.service.DietAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diet-analysis")
public class DietAnalysisController {

    private final DietAnalysisService dietAnalysisService;

    @PostMapping(value = "/message")
    public ResponseEntity<DietAnalysisResponse> DietAnalysis(
            @RequestParam("message") String message,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "mealTime", required = false) String mealTime) { // mealTime 파라미터 추가
        try {
            System.out.println("컨트롤러 메세지: " + message);
            System.out.println("컨트롤러 이메일: " + email);
            System.out.println("컨트롤러 mealTime: " + mealTime); // mealTime 로그 추가

            // message를 앞뒤 공백을 제거한 후 빈 문자열인지 혹은 null인지 확인
            if (message == null || message.trim().isEmpty()) {
                System.out.println("message가 비어 있음");
                // 빈 데이터 리턴(분석 결과 없음)
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

            DietAnalysisRequest dietAnalysisRequest = new DietAnalysisRequest(message, email, mealTime);
            DietAnalysisResponse response = dietAnalysisService.getDietAnalysisResponse(dietAnalysisRequest);
            System.out.println("컨트롤러 (서비스에서 넘어온 전체값): " + response);
            System.out.println("컨트롤러 (foodList): " + response.getFoodList());
            System.out.println("컨트롤러 (nutritionPerFood): " + response.getNutritionPerFood());
            System.out.println("컨트롤러 (totalNutrition): " + response.getTotalNutrition());
            System.out.println("컨트롤러 (deficientNutrients): " + response.getDeficientNutrients());
            System.out.println("컨트롤러 (nextMealSuggestion): " + response.getNextMealSuggestion());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("컨트롤러 오류 발생: " + e.getMessage());
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