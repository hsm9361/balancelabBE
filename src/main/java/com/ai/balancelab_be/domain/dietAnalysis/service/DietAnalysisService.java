package com.ai.balancelab_be.domain.dietAnalysis.service;

import com.ai.balancelab_be.domain.dietAnalysis.dto.DietAnalysisRequest;
import com.ai.balancelab_be.domain.dietAnalysis.dto.DietAnalysisResponse;
import com.ai.balancelab_be.domain.dietAnalysis.dto.FoodNutrition;
import com.ai.balancelab_be.domain.dietAnalysis.dto.Nutrition;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DietAnalysisService {

    @Value("${fastapi.url:http://localhost:8000}")
    private String fastApiUrl;

    private final RestTemplate restTemplate;

    public DietAnalysisService() {
        this.restTemplate = new RestTemplate();
    }

    public DietAnalysisResponse getFoodNameResponse(DietAnalysisRequest request) {
        try {
            String url = fastApiUrl + "/analysis/foodName";
            System.out.println("서비스단체크: FastAPI 요청 URL: " + url);
            System.out.println("서비스단체크: 요청 데이터: " + request.getMessage());

            DietAnalysisResponse response = restTemplate.postForObject(url, request, DietAnalysisResponse.class);
            if (response != null) {
                System.out.println("서비스단체크: foodList: " + response.getFoodList());
                System.out.println("서비스단체크: nutritionPerFood: " + response.getNutritionPerFood());
                System.out.println("서비스단체크: totalNutrition: " + response.getTotalNutrition());
                System.out.println("서비스단체크: deficientNutrients: " + response.getDeficientNutrients());
                System.out.println("서비스단체크: nextMealSuggestion: " + response.getNextMealSuggestion());
                return response;
            } else {
                System.out.println("서비스단체크: FastAPI로부터 응답이 null입니다.");
                return new DietAnalysisResponse(
                        Collections.emptyList(),
                        Collections.emptyList(),
                        new Nutrition(0, 0, 0, 0, 0, 0, 0),
                        Collections.emptyList(),
                        Collections.emptyList()
                );
            }
        } catch (Exception e) {
            System.err.println("서비스단체크: FastAPI 호출 실패: " + e.getMessage());
            e.printStackTrace();
            return new DietAnalysisResponse(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    new Nutrition(0, 0, 0, 0, 0, 0, 0),
                    Collections.emptyList(),
                    Collections.emptyList()
            );
        }
    }
}