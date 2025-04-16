package com.ai.balancelab_be.domain.calendar.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DailyDietRecordRequestDto {
    private int userId;
    private String foodName;
    private String category;
    private float intakeAmount;
    private Date eatenDate;
}
