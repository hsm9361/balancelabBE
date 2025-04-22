package com.ai.balancelab_be.domain.foodRecord.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDateTime;

@Data
@Builder
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
    private Date consumedDate;
}
