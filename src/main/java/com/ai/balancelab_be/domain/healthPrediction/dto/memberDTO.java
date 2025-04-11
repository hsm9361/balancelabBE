package com.ai.balancelab_be.domain.healthPrediction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class memberDTO {
    private int age;
    private double height;
    private double weight;
    private String gender;
    private double carbo_perday;
    private double sugar_perday;
    private double fat_perday;
    private double sodium_perday;
    private double fibrin_perday;
    private double water_perday;
} 