package com.ai.balancelab_be.domain.foodRecord.entity;

import com.ai.balancelab_be.domain.member.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_DAILY_NUTRITION_RECORD",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"member_id", "record_date"})}
)
@Getter
@Setter
public class DailyNutritionRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, referencedColumnName = "id")
    private MemberEntity memberEntity;

    private double calories;
    private double protein;
    private double carbo;
    private double sugar;
    private double fat;
    private double sodium;
    private double fibrin;
    private double water;

    @Column(name = "consumed_date", nullable = false)
    private LocalDate consumedDate;

    @CreationTimestamp
    @Column(name = "reg_date")
    private LocalDateTime regDate;

    @UpdateTimestamp
    @Column(name = "upd_date")
    private LocalDateTime updDate;
}
