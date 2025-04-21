package com.ai.balancelab_be.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_GOAL_NUTRITION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalNutritionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @Comment("TDEE 칼로리")
    @Column(name="tdee_calories")
    private double tdeeCalories;

    @Comment("목표 칼로리")
    @Column(name="goal_calories")
    private double goalCalories;

    @Comment("목표 탄수화물 (g)")
    @Column(name="goal_carbo")
    private double goalCarbo;

    @Comment("목표 단백질 (g)")
    @Column(name="goal_protein")
    private double goalProtein;

    @Comment("목표 지방 (g)")
    @Column(name="goal_fat")
    private double goalFat;

    @CreationTimestamp
    @Column(name = "reg_date")
    private LocalDateTime insDate;

    @UpdateTimestamp
    @Column(name = "upd_date")
    private LocalDateTime updDate;

}
