package com.ai.balancelab_be.domain.auth.controller;

import com.ai.balancelab_be.domain.member.dto.MemberDto;
import com.ai.balancelab_be.global.security.TokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final TokenProvider tokenProvider;

    public AuthController(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            String token = refreshToken.substring(7);
            
            if (tokenProvider.validateToken(token)) {
                Authentication authentication = tokenProvider.getAuthentication(token);
                Long memberId = tokenProvider.getMemberIdFromToken(token);
                String newAccessToken = tokenProvider.createAccessToken(authentication, memberId);
                String newRefreshToken = tokenProvider.createRefreshToken(authentication, memberId);

                MemberDto response = MemberDto.builder()
                        .email(authentication.getName())
                        .type("TOKEN_REFRESH_SUCCESS")
                        .accessToken(newAccessToken)
                        .refreshToken(newRefreshToken)
                        .build();

                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.status(401).body(
            MemberDto.builder()
                .type("TOKEN_REFRESH_FAILURE")
                .build()
        );
    }
} 