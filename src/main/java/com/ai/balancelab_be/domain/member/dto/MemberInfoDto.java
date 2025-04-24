package com.ai.balancelab_be.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoDto {
    private Long id;
    private String username;
    private String membername;
    private String email;
    private String sub;
    private Integer age;
    private Double height;
    private Double weight;
    private String gender;
    private String activityLevel;
    private Double goalWeight;
    private String profileImageUrl;
    private LocalDate birthDate;
}
