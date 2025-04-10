package com.ai.balancelab_be.domain.member.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MemberDto {
    private String username;
    private String email;
    private String type;
    private String accessToken;
    private String refreshToken;

}

