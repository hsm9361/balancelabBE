package com.ai.balancelab_be.domain.foodRecord.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class FoodRecordCountDto {
    private Long count;
    private LocalDateTime consumedDate;

    public FoodRecordCountDto(Long count, LocalDateTime consumedDate) {
        this.count = count;
        this.consumedDate = consumedDate;
    }

    // Getter, Setter
    public Long getCount() {
        return count;
    }

    public LocalDateTime getConsumedDate() {
        return consumedDate;
    }

}
