package com.ai.balancelab_be.domain.dietAnalysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DietAnalysisRequest {
    private String message;
    private String email;
    private String mealTime;

    public DietAnalysisRequest(String message, String email, String mealTime) {
        this.message = message;
        this.email = email;
        this.mealTime = mealTime;
    }

    public DietAnalysisRequest(String message, String email) {
        this.message = message;
        this.email = email;
    }

    public DietAnalysisRequest(String message) {
        this.message = message;
    }
}
