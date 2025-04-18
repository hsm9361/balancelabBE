package com.ai.balancelab_be.domain.healthPrediction.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HealthPredictionRequest {
    private Long memberId;
    private int age;
    private double height;
    private double weight;
    private int gender;
    private int smokeDaily;
    private int drinkWeekly;
    private int exerciseWeekly;
    private int historyDiabetes;
    private int historyHypertension;
    private int historyCardiovascular;
    private double dailyCarbohydrate;
    private double dailySugar;
    private double dailyFat;
    private double dailySodium;
    private double dailyFibrin;
    private double dailyWater;
} 