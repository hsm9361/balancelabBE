package com.ai.balancelab_be.domain.calendar.dto;

import lombok.*;

import java.util.Date;

@Data

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class DailyDietRecordRequestDto {
    private int foodId;
    private int userId;
    private String foodName;
    private String category;
    private float intakeAmount;
    private Date eatenDate;
}