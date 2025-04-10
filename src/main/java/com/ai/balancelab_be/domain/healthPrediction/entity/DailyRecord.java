package com.ai.balancelab_be.domain.healthPrediction.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "daily_record")
@Getter

public class DailyRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDate recordDate;
    private double carbo;
    private double sugar;
    private double fat;
    private double sodium;
    private double fibrin;
    private double water;
} 