package com.ai.balancelab_be.domain.imageAnalysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class FoodAnalysisResponse {
    private String food_name;
    private int calories;
    private Nutrients nutrients;

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class Nutrients {
        private int carbohydrates;
        private int fat;
        private int sugar;
        private int sodium;
        private int fiber;
        private int water;
        // getters and setters
    }
}
