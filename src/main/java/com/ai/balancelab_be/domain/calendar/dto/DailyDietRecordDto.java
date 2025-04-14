package com.ai.balancelab_be.domain.calendar.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class DailyDietRecordDto {
    private int foodId;
    private String foodName;
    private String category;
    private float intakeAmount;
    private String unit;
    private LocalDateTime eatenDate;

}
