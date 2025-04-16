package com.ai.balancelab_be.domain.healthPrediction.dto;

import lombok.Data;

@Data
public class HealthPredictionResponse {
    private double diabetes_proba;
    private double hypertension_proba;
    private double cdv_proba;
}
