package com.ai.balancelab_be.domain.foodRecord.entity;

import com.ai.balancelab_be.domain.member.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_FOOD_RECORD",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"member_id", "consumed_date"})}
)
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
    private Double carbohydrates;

    @Comment("지방")
    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double fat;

    @Comment("섬유질")
    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double fiber;

    @Comment("단백질")
    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double protein;

    @Comment("나트륨")
    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double sodium;

    @Comment("설탕")
    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double sugar;

    @Comment("물")
    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double water;

    @Comment("타입 (image, text, custom)")
    @Column(nullable = false)
    private String type;

    @Comment("단위 (g, serving)")
    @Column(nullable = false)
    private String unit;

    @Comment("먹은 양")
    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double amount;

    @Comment("식사 시간")
    @Column(name = "meal_time", nullable = false)
    private String mealTime;

    @Comment("식사 날짜")
    @Column(name="consumed_date", nullable = false)
    private LocalDate consumedDate;

    @CreationTimestamp
    @Column(name = "reg_date")
    private LocalDateTime regDate;

    @UpdateTimestamp
    @Column(name = "upt_date")
    private LocalDateTime uptDate;
}