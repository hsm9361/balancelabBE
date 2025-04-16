package com.ai.balancelab_be.domain.dietAnalysis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "deficient_nutrient")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeficientNutrient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long groupId;

    private boolean protein;
    private boolean carbohydrate;
    private boolean water;
    private boolean sugar;
    private boolean fat;
    private boolean fiber;
    private boolean sodium;

    private LocalDate regDate;
}
