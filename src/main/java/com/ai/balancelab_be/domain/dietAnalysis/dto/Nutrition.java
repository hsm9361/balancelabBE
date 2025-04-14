package com.ai.balancelab_be.domain.dietAnalysis.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Nutrition {
    private double protein;
    private double carbohydrate;
    private double water;
    private double sugar;
    private double fat;
    private double fiber;
    private double sodium;

    // 기본 생성자
    public Nutrition() {}

    // 전체 필드 생성자
    public Nutrition(double protein, double carbohydrate, double water, double sugar, double fat, double fiber, double sodium) {
        this.protein = protein;
        this.carbohydrate = carbohydrate;
        this.water = water;
        this.sugar = sugar;
        this.fat = fat;
        this.fiber = fiber;
        this.sodium = sodium;
    }
}