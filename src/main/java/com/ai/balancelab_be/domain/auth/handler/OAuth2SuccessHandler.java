package com.ai.balancelab_be.domain.auth.handler;

import com.ai.balancelab_be.domain.auth.service.AuthMemberService;
import com.ai.balancelab_be.domain.member.dto.MemberDto;
import com.ai.balancelab_be.global.security.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;
    private final AuthMemberService authMemberService;

    public OAuth2SuccessHandler(TokenProvider tokenProvider, ObjectMapper objectMapper, AuthMemberService authMemberService) {
        this.tokenProvider = tokenProvider;
        this.objectMapper = objectMapper;
        this.authMemberService = authMemberService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");
        log.info("✅ OAuth2 인증 성공 - 사용자 이메일: {}", email);


        // 트랜잭션 서비스에서 저장
        authMemberService.saveIfNotExists(email);

        // 리다이렉트
        MemberDto memberDto = MemberDto.builder()
                .username(email)
                .email(email)
                .type("LOGIN_SUCCESS")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        String memberDtoJson = objectMapper.writeValueAsString(memberDto);
        String encodedData = URLEncoder.encode(memberDtoJson, StandardCharsets.UTF_8);
        response.sendRedirect("http://localhost:3000/oauth/callback?data=" + encodedData);
    }
}

