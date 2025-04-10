package com.ai.balancelab_be.domain.dietAnalysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nutrition {
    @JsonProperty("protein")
    private double protein;

    @JsonProperty("fat")
    private double fat;

    @JsonProperty("carbohydrate")
    private double carbohydrate;

    @JsonProperty("fiber")
    private double fiber;

    @JsonProperty("calcium")
    private double calcium;

    @JsonProperty("sodium")
    private double sodium;
}