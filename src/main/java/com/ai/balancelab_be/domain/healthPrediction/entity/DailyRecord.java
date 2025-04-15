package com.ai.balancelab_be.domain.healthPrediction.entity;

import com.ai.balancelab_be.domain.member.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_daily_record")
@Getter

public class DailyRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;


    private double carbo;
    private double sugar;
    private double fat;
    private double sodium;
    private double fibrin;
    private double water;

    @CreationTimestamp
    @Column(name = "reg_date")
    private LocalDateTime regDate;

    @UpdateTimestamp
    @Column(name = "upd_date")
    private LocalDateTime updDate;
}
