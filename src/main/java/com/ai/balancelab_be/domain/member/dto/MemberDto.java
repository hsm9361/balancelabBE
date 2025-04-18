package com.ai.balancelab_be.domain.member.dto;

import com.ai.balancelab_be.domain.member.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MemberDto {
    private long id;
    private String username;
    private String membername;
    private String email;
    private String type;
    private String accessToken;
    private String refreshToken;
    private String hasRequiredInfo;
    private String activityLevel;
    private String sub;
    private int age;
    private double height;
    private double weight;
    private String gender;
    private double goalWeight;
    private double carbo_perday;
    private double sugar_perday;
    private double fat_perday;
    private double sodium_perday;
    private double fibrin_perday;
    private double water_perday;

    public static MemberDto fromEntity(MemberEntity member) {
        return MemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .username(member.getUsername())
                .membername(member.getMembername())
                .age(member.getAge())
                .height(member.getHeight())
                .weight(member.getWeight())
                .activityLevel(member.getActivityLevel())
                .gender(member.getGender())
                .goalWeight(member.getGoalWeight())
                .type("LOGIN_SUCCESS")
                .accessToken("dummy_access")   // 실제 토큰 로직 추가 가능
                .refreshToken("dummy_refresh") // 실제 토큰 로직 추가 가능
                .build();
    }
}