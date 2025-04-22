package com.ai.balancelab_be.domain.member.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class WeightHistoryDTO {
    private Long id;
    private double weight;
    private LocalDateTime insDate;

}