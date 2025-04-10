package com.ai.balancelab_be.domain.healthPrediction.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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