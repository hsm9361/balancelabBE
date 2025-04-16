package com.ai.balancelab_be.domain.healthPrediction.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HealthPredictionRequest {
    private int age;
    private String gender;
    private double carbo;
    private double sugar;
    private double fat;
    private double sodium;
    private double fibrin;
    private double water;
} 