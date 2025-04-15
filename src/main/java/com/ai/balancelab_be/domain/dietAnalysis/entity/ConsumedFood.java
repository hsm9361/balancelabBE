package com.ai.balancelab_be.domain.dietAnalysis.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "consumed_food")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsumedFood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String food;

    private Long groupId;

    private String email;

    private String mealTime;

    private double protein;
    private double carbohydrate;
    private double water;
    private double sugar;
    private double fat;
    private double fiber;
    private double sodium;

    private LocalDate createdDate;
}
