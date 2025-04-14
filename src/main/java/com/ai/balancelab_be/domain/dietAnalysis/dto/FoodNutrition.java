package com.ai.balancelab_be.domain.dietAnalysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FoodNutrition {
    @JsonProperty("food")
    private String food;

    @JsonProperty("nutrition")
    private Nutrition nutrition;

    public FoodNutrition() {}

    public FoodNutrition(String food, Nutrition nutrition) {
        this.food = food;
        this.nutrition = nutrition;
    }
}