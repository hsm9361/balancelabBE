package com.ai.balancelab_be.domain.foodRecord.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyNutritionRecordDto {

    private Long id;

    @NotNull(message = "Member ID is required")
    private Long memberId;

    @PositiveOrZero(message = "Calories must be positive or zero")
    private double calories;

    @PositiveOrZero(message = "Protein must be positive or zero")
    private double protein;

    @PositiveOrZero(message = "Carbohydrates must be positive or zero")
    private double carbo;

    @PositiveOrZero(message = "Fat must be positive or zero")
    private double fat;

    @NotNull(message = "Consumed date is required")
    private LocalDate consumedDate;
}