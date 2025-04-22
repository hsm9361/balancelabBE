package com.ai.balancelab_be.domain.member.dto;

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

public class GoalNutritionDto {
    private Long id;

    private Long memberId;

    private double tdeeCalories;

    private double goalCalories;

    private double goalProtein;

    private double goalCarbo;

    private double goalFat;
}
