package com.ai.balancelab_be.domain.healthPrediction.entity;

import com.ai.balancelab_be.domain.member.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_predict_record")
@Getter
@Builder

public class PredictRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    private double dailyCarbohydrate;
    private double dailySugar;
    private double dailyFat;
    private double dailySodium;
    private double dailyFibrin;
    private double dailyWater;
    private int historyDiabetes;
    private int historyHypertension;
    private int historyCvd;
    private double diabetesProba;
    private double hypertensionProba;
    private double cvdProba;
    private int smokeDaily;
    private int drinkWeekly;
    private int exerciseWeekly;

    @CreationTimestamp
    @Column(name = "reg_date")
    private LocalDateTime regDate;

    @UpdateTimestamp
    @Column(name = "upd_date")
    private LocalDateTime updDate;
}