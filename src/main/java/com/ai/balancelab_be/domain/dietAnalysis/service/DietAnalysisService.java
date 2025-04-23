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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DietAnalysisService {

    @Value("${fastapi.url:http://localhost:8000}")
    private String fastApiUrl;

    private final RestTemplate restTemplate;
    private final ConsumedFoodRepository consumedFoodRepository;
    private final DeficientNutrientRepository deficientNutrientRepository;
    private final RecommendedMealRepository recommendedMealRepository;

    public DietAnalysisResponse fetchFastApiResponse(DietAnalysisRequest request) {
        try {
            String url = fastApiUrl + "/analysis/diet";
            DietAnalysisResponse response = restTemplate.postForObject(url, request, DietAnalysisResponse.class);
            if (response != null && response.getFoodList() != null) {
                List<String> foodList = response.getFoodList().stream()
                        .filter(food -> food != null && !food.trim().isEmpty() && !isInvalidFoodEntry(food))
                        .collect(Collectors.toList());

                if (foodList.isEmpty()) {
                    return new DietAnalysisResponse(
                            Collections.emptyList(),
                            Collections.emptyList(),
                            new Nutrition(0, 0, 0, 0, 0, 0, 0),
                            Collections.emptyList(),
                            Collections.emptyList()
                    );
                }

                return new DietAnalysisResponse(
                        foodList,
                        response.getNutritionPerFood(),
                        response.getTotalNutrition(),
                        response.getDeficientNutrients(),
                        response.getNextMealSuggestion()
                );
            } else {
                return new DietAnalysisResponse(
                        Collections.emptyList(),
                        Collections.emptyList(),
                        new Nutrition(0, 0, 0, 0, 0, 0, 0),
                        Collections.emptyList(),
                        Collections.emptyList()
                );
            }
        } catch (Exception e) {
            System.err.println("서비스단체크(FastAPI 호출 실패): " + e.getMessage());
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

    private boolean isInvalidFoodEntry(String food) {
        if (food == null) return true;
        String trimmed = food.trim();
        return trimmed.isEmpty() ||
                trimmed.equals("[]") ||
                trimmed.equals("[\"\"]") ||
                trimmed.matches("(?s).*```.*\\[\\].*```.*"); // 수정된 정규 표현식
    }

    @Transactional
    public void saveDietAnalysis(DietAnalysisRequest request, DietAnalysisResponse response) {
        Long groupId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;

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

    @Transactional
    public DietAnalysisResponse getDietAnalysisResponse(DietAnalysisRequest request) {
        DietAnalysisResponse response = fetchFastApiResponse(request);
//        saveDietAnalysis(request, response);
        return response;
    }
}