package com.ai.balancelab_be.domain.dietAnalysis.service;

import com.ai.balancelab_be.domain.dietAnalysis.dto.DietAnalysisRequest;
import com.ai.balancelab_be.domain.dietAnalysis.dto.DietAnalysisResponse;
import com.ai.balancelab_be.domain.dietAnalysis.dto.FoodNutrition;
import com.ai.balancelab_be.domain.dietAnalysis.dto.Nutrition;
import com.ai.balancelab_be.domain.dietAnalysis.entity.ConsumedFood;
import com.ai.balancelab_be.domain.dietAnalysis.entity.DeficientNutrient;
import com.ai.balancelab_be.domain.dietAnalysis.entity.RecommendedMeal;
import com.ai.balancelab_be.domain.dietAnalysis.repository.ConsumedFoodRepository;
import com.ai.balancelab_be.domain.dietAnalysis.repository.DeficientNutrientRepository;
import com.ai.balancelab_be.domain.dietAnalysis.repository.RecommendedMealRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DietAnalysisService {

    @Value("${fastapi.url:http://localhost:8000}")
    private String fastApiUrl;

    private final RestTemplate restTemplate;
    private final ConsumedFoodRepository consumedFoodRepository;
    private final DeficientNutrientRepository deficientNutrientRepository;
    private final RecommendedMealRepository recommendedMealRepository;

    // FastAPI로부터 분석 결과 가져오기
    public DietAnalysisResponse fetchFastApiResponse(DietAnalysisRequest request) {
        try {
            String url = fastApiUrl + "/analysis/foodName";
            System.out.println("서비스단체크 (요청 데이터): " + request.getMessage());

            DietAnalysisResponse response = restTemplate.postForObject(url, request, DietAnalysisResponse.class);
            if (response != null && response.getFoodList() != null && !response.getFoodList().isEmpty()) {
                System.out.println("서비스단체크(foodList) " + response.getFoodList());
                System.out.println("서비스단체크(nutritionPerFood) " + response.getNutritionPerFood());
                System.out.println("서비스단체크(totalNutrition) " + response.getTotalNutrition());
                System.out.println("서비스단체크(deficientNutrients) " + response.getDeficientNutrients());
                System.out.println("서비스단체크(nextMealSuggestion) " + response.getNextMealSuggestion());
                return response;
            } else {
                System.out.println("서비스단체크: FastAPI로부터 응답이 null이거나 foodList가 비어 있습니다.");
                // 빈객체 생성(분석 결과 없음을 나타내서 안전한 응답 제공)
                return new DietAnalysisResponse(
                        Collections.emptyList(),
                        Collections.emptyList(),
                        new Nutrition(0, 0, 0, 0, 0, 0, 0),
                        Collections.emptyList(),
                        Collections.emptyList()
                );
            }
        } catch (Exception e) {
            System.err.println("서비스단체크(FastAPI 호출 실패) " + e.getMessage());
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

    // DB에 분석 결과 저장
    @Transactional
    public void saveDietAnalysis(DietAnalysisRequest request, DietAnalysisResponse response) {
        // groupId 생성
        Long groupId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;

        // ConsumedFood 저장
        List<FoodNutrition> nutritionPerFood = response.getNutritionPerFood() != null ?
                response.getNutritionPerFood() : Collections.emptyList();
        Long memberId = request.getMemberId();
        String mealTime = request.getMealTime();

        for (FoodNutrition foodNutrition : nutritionPerFood) {
            if (foodNutrition.getFood() == null || foodNutrition.getNutrition() == null) {
                System.err.println("서비스단체크: nutritionPerFood 항목 누락: " + foodNutrition);
                continue;
            }

            ConsumedFood consumedFood = new ConsumedFood();
            consumedFood.setFood(foodNutrition.getFood());
            consumedFood.setGroupId(groupId);
            consumedFood.setMemberId(memberId);
            consumedFood.setMealTime(mealTime);
            Nutrition nutrition = foodNutrition.getNutrition();
            consumedFood.setProtein(nutrition.getProtein());
            consumedFood.setCarbohydrate(nutrition.getCarbohydrate());
            consumedFood.setWater(nutrition.getWater());
            consumedFood.setSugar(nutrition.getSugar());
            consumedFood.setFat(nutrition.getFat());
            consumedFood.setFiber(nutrition.getFiber());
            consumedFood.setSodium(nutrition.getSodium());
            consumedFood.setRegDate(LocalDate.now());
            consumedFoodRepository.save(consumedFood);
        }

        // DeficientNutrient 저장
        DeficientNutrient deficientNutrient = new DeficientNutrient();
        deficientNutrient.setGroupId(groupId);
        deficientNutrient.setRegDate(LocalDate.now());

        List<String> deficientNutrients = response.getDeficientNutrients() != null ?
                response.getDeficientNutrients() : Collections.emptyList();
        deficientNutrient.setProtein(deficientNutrients.contains("단백질"));
        deficientNutrient.setCarbohydrate(deficientNutrients.contains("탄수화물"));
        deficientNutrient.setWater(deficientNutrients.contains("수분"));
        deficientNutrient.setSugar(deficientNutrients.contains("당"));
        deficientNutrient.setFat(deficientNutrients.contains("지방"));
        deficientNutrient.setFiber(deficientNutrients.contains("식이섬유") || deficientNutrients.contains("섬유질"));
        deficientNutrient.setSodium(deficientNutrients.contains("나트륨"));
        deficientNutrientRepository.save(deficientNutrient);

        // RecommendedMeal 저장
        List<String> nextMealSuggestions = response.getNextMealSuggestion() != null ?
                response.getNextMealSuggestion() : Collections.emptyList();
        for (String meal : nextMealSuggestions) {
            RecommendedMeal recommendedMeal = new RecommendedMeal();
            recommendedMeal.setGroupId(groupId);
            recommendedMeal.setMeal(meal);
            recommendedMeal.setRegDate(LocalDate.now());
            recommendedMealRepository.save(recommendedMeal);
        }
    }

    // FastAPI 호출 + DB 저장
    @Transactional
    public DietAnalysisResponse getDietAnalysisResponse(DietAnalysisRequest request) {
        // 1. FastAPI로부터 결과 가져오기
        DietAnalysisResponse response = fetchFastApiResponse(request);

        // 2. DB에 저장
//        saveDietAnalysis(request, response);

        // 3. 결과 반환
        return response;
    }
}