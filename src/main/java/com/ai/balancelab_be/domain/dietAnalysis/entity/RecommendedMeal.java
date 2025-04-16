package com.ai.balancelab_be.domain.dietAnalysis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "recommended_meal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedMeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long groupId;

    private String meal;

    private LocalDate regDate;
}
