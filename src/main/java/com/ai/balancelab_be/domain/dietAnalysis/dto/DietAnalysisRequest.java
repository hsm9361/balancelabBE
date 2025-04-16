package com.ai.balancelab_be.domain.dietAnalysis.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DietAnalysisRequest {
    private String message;
    private Long memberId;
    private String mealTime;

    public DietAnalysisRequest(String message, Long memberId, String mealTime) {
        this.message = message;
        this.memberId = memberId;
        this.mealTime = mealTime;
    }

    public DietAnalysisRequest(String message, Long memberId) {
        this.message = message;
        this.memberId = memberId;
    }

    public DietAnalysisRequest(String message) {
        this.message = message;
    }
}
