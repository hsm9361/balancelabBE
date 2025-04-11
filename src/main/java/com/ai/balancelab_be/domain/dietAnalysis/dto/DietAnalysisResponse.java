package com.ai.balancelab_be.domain.dietAnalysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietAnalysisResponse {
    @JsonProperty("food_list")
    private List<String> foodList;

    @JsonProperty("nutrition")
    private Nutrition nutrition;

    @JsonProperty("deficient_nutrients")
    private List<String> deficientNutrients;

    @JsonProperty("next_meal_suggestion")
    private List<String> nextMealSuggestion;
}