package com.ai.balancelab_be.domain.foodRecord.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_FOOD_RECORD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Comment("음식명")
    @Column(nullable = false)
    private String foodName;

    @Comment("그룹 ID")
    @Column(name = "group_id")
    private String groupId;

    @Comment("회원 ID")
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Comment("탄수화물")
    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double carbohydrates = 0.0;

    @Comment("지방")
    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double fat = 0.0;

    @Comment("섬유질")
    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double fiber = 0.0;

    @Comment("단백질")
    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double protein = 0.0;

    @Comment("나트륨")
    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double sodium = 0.0;

    @Comment("설탕")
    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double sugar = 0.0;

    @Comment("물")
    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double water = 0.0;

    @Comment("타입 (image, text, custom)")
    @Column(nullable = false)
    private String type;

    @Comment("단위 (g, serving)")
    @Column(nullable = false)
    private String unit;

    @Comment("먹은 양")
    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double amount = 0.0;

    @Comment("식사 시간")
    @Column(name = "meal_time", nullable = false)
    private String mealTime;

    @CreationTimestamp
    @Column(name = "reg_date")
    private LocalDateTime regDate;

    @UpdateTimestamp
    @Column(name = "upt_date")
    private LocalDateTime uptDate;
}