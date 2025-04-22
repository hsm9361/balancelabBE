package com.ai.balancelab_be.domain.challenge.entitiy;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    private Long memberId;  // 사용자 식별자 (사용자 ID)

    private String goal;  // 목표 (예: 다이어트, 건강관리 등)

    private String period;  // 기간 (개월, 년)

    private Integer targetWeight;  // 목표 체중

    private LocalDate startDate;  // 시작 날짜

    private LocalDate endDate;  // 종료 날짜

    private boolean isCompleted;  // 완료 여부

    private LocalDate regDate;  // 챌린지 등록 날짜

}
