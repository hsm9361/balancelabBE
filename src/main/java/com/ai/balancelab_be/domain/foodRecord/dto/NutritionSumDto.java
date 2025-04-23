package com.ai.balancelab_be.domain.foodRecord.dto;

import lombok.*;

import java.sql.Date;

@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NutritionSumDto {
    private Double sumCalories;
    private Double sumCarbohydrates;
    private Double sumProtein;
    private Double sumFiber;
    private Double sumSugar;
    private Double sumSodium;
    private Double sumFat;
    private Double sumWater;
    private Date consumedDate;
}
