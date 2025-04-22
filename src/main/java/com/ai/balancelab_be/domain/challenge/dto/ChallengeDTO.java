package com.ai.balancelab_be.domain.challenge.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ChallengeDTO {

    private Long memberId;  // 사용자 ID

    private String goal;  // 목표 (다이어트, 건강관리, 근성장 등)

    private String period;  // 기간 (개월, 년)

    private Integer targetWeight;  // 목표 체중

    private LocalDate startDate;  // 시작 날짜

    private LocalDate endDate;  // 종료 날짜
}
