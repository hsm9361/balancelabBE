package com.ai.balancelab_be.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "TB_WEIGHT_HISTORY")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeightHistoryEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @Comment("몸무게")
    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double weight;

    @CreationTimestamp
    @Column(name = "reg_date")
    private LocalDateTime insDate;

    @UpdateTimestamp
    @Column(name = "upd_date")
    private LocalDateTime updDate;

}
