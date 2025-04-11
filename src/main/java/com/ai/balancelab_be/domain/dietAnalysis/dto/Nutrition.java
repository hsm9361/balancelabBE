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

    @JsonProperty("carbohydrate")
    private double carbohydrate;

    @JsonProperty("water")
    private double water;

    @JsonProperty("sugar")
    private double sugar;

    @JsonProperty("fat")
    private double fat;

    @JsonProperty("fiber")
    private double fiber;

    @JsonProperty("sodium")
    private double sodium;
}