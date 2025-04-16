package com.ai.balancelab_be.domain.calendar.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyDietRecordDto {
    private String foodName;
    private String category;
    private float intakeAmount;
    private String unit;
    private LocalDate eatenDate;

}
