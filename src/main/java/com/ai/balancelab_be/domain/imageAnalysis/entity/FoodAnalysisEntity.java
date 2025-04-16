package com.ai.balancelab_be.domain.imageAnalysis.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "TB_FOOD_ANALYSIS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodAnalysisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("식단 분석 ID")
    private Long id;

    @Comment("사용자 ID")
    private Long userId;

    @Comment("음식 이름")
    private String foodName;

    @Comment("칼로리(kcal)")
    private int calories;

    @Comment("탄수화물 (g)")
    private int carbohydrates;

    @Comment("지방 (g)")
    private int fat;

    @Comment("당류 (g)")
    private int sugar;

    @Comment("나트륨 (mg)")
    private int sodium;

    @Comment("식이섬유 (g)")
    private int fiber;

    @Comment("수분 (ml)")
    private int water;
}

