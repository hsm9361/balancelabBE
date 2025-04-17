package com.ai.balancelab_be.domain.foodRecord.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodRecordDto {

    private Long id;

    @NotBlank(message = "Food name is required")
    private String foodName;

    private String groupId;

    @Positive(message = "Member ID must be positive")
    private Long memberId;

    @Positive(message = "Carbohydrates must be positive or zero")
    private Double carbohydrates;

    @Positive(message = "Fat must be positive or zero")
    private Double fat;

    @Positive(message = "Fiber must be positive or zero")
    private Double fiber;

    @Positive(message = "Protein must be positive or zero")
    private Double protein;

    @Positive(message = "Sodium must be positive or zero")
    private Double sodium;

    @Positive(message = "Sugar must be positive or zero")
    private Double sugar;

    @Positive(message = "Water must be positive or zero")
    private Double water;

    @Size(max = 10, message = "Type must be less than 10 characters")
    private String type;

    @Size(max = 10, message = "Unit must be less than 10 characters")
    private String unit;

    @Positive(message = "Amount must be positive")
    private Double amount;

    private String mealTime;

    private LocalDateTime regDate;

    private LocalDateTime uptDate;

    private LocalDateTime consumedDate;
}