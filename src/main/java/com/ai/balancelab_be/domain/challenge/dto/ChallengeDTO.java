package com.ai.balancelab_be.domain.challenge.dto;

import com.ai.balancelab_be.domain.challenge.entitiy.Challenge;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ChallengeDTO {

    private Long memberId;

    private String goal;

    private String period;

    private String periodUnit;

    private Integer startWeight;

    private Integer targetWeight;

    private LocalDate startDate;

    private LocalDate endDate;

    private Challenge.ChallengeStatus status;
}
