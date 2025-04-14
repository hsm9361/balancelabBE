package com.ai.balancelab_be.domain.dietAnalysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DietAnalysisResponse {
    @JsonProperty("food_list")
    private List<String> foodList;

    @JsonProperty("nutrition_per_food")
    private List<FoodNutrition> nutritionPerFood;

    @JsonProperty("total_nutrition")
    private Nutrition totalNutrition;

    @JsonProperty("deficient_nutrients")
    private List<String> deficientNutrients;

    @JsonProperty("next_meal_suggestion")
    private List<String> nextMealSuggestion;

    public DietAnalysisResponse() {}

    public DietAnalysisResponse(List<String> foodList, List<FoodNutrition> nutritionPerFood,
                                Nutrition totalNutrition, List<String> deficientNutrients,
                                List<String> nextMealSuggestion) {
        this.foodList = foodList;
        this.nutritionPerFood = nutritionPerFood;
        this.totalNutrition = totalNutrition;
        this.deficientNutrients = deficientNutrients;
        this.nextMealSuggestion = nextMealSuggestion;
    }
}