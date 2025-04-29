package com.ai.balancelab_be.domain.challenge.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private String goal;

    private String period;

    private String periodUnit;

    private double startWeight;

    private double targetWeight;

    private LocalDate startDate;

    private LocalDate endDate;

    private boolean isCompleted;

    private LocalDate regDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChallengeStatus status = ChallengeStatus.ONGOING;

    public enum ChallengeStatus {
        ONGOING, COMPLETED, FAILED
    }

}
