package com.ai.balancelab_be.domain.calendar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dailydiet_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyDietRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private int foodId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "food_name", length = 100)
    private String foodName;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "intake_amount")
    private float intakeAmount;

    @Column(name = "unit", length = 10)
    private String unit;

    @Column(name = "eaten_date")
    private LocalDateTime eatenDate;

    @Column(name = "ins_date")
    private LocalDateTime insDate;

    @Column(name = "upt_date")
    private LocalDateTime uptDate;

    @PrePersist
    protected void onCreate() {
        this.insDate = LocalDateTime.now();
        this.uptDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.uptDate = LocalDateTime.now();
    }
}
