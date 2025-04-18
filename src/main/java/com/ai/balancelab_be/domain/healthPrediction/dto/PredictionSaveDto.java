package com.ai.balancelab_be.domain.healthPrediction.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PredictionSaveDto {
    private Long memberId;
    private double dailyCarbohydrate;
    private double dailySugar;
    private double dailyFat;
    private double dailySodium;
    private double dailyFibrin;
    private double dailyWater;
    private int historyDiabetes;
    private int historyHypertension;
    private int historyCvd;
    private double diabetesProba;
    private double hypertensionProba;
    private double cvdProba;
    private int smokeDaily;
    private int drinkWeekly;
    private int exerciseWeekly;
}
