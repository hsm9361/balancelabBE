package com.ai.balancelab_be.domain.auth.handler;

import com.ai.balancelab_be.domain.auth.service.AuthMemberService;
import com.ai.balancelab_be.domain.member.dto.MemberDto;
import com.ai.balancelab_be.domain.member.entity.MemberEntity;
import com.ai.balancelab_be.global.security.CustomUserDetails;
import com.ai.balancelab_be.global.security.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");
        String sub = (String) oAuth2User.getAttributes().get("sub");
        String username = (String) oAuth2User.getAttributes().get("name");
        log.info("✅ OAuth2 인증 성공 - 사용자 이메일: {}", email);
        log.info("✅ OAuth2 인증 성공 - 사용자 sub: {}", sub);

        // 트랜잭션 서비스에서 저장하고 MemberEntity 반환
        MemberEntity member = authMemberService.saveIfNotExists(email, sub, username);

        // CustomUserDetails 생성
        CustomUserDetails userDetails = new CustomUserDetails(member);
        Authentication newAuth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // 필수 정보 존재 여부 확인
        boolean hasRequiredInfo = isRequiredInfoComplete(member);

        // 토큰 생성 시 member ID 포함
        String accessToken = tokenProvider.createAccessToken(newAuth, member.getId());
        String refreshToken = tokenProvider.createRefreshToken(newAuth, member.getId());

        // 리다이렉트
        MemberDto memberDto = MemberDto.builder()
                .username(email)
                .email(email)
                .type("LOGIN_SUCCESS")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .hasRequiredInfo(hasRequiredInfo ? "y" : "n")
                .build();

        String memberDtoJson = objectMapper.writeValueAsString(memberDto);
        String encodedData = URLEncoder.encode(memberDtoJson, StandardCharsets.UTF_8);
        response.sendRedirect("http://localhost:3000/oauth/callback?data=" + encodedData);
    }

    private boolean isRequiredInfoComplete(MemberEntity member) {
        return member.getHeight() > 0.0 &&
                member.getWeight() > 0.0 &&
                member.getAge() > 0 &&
                member.getGender() != null && !member.getGender().isEmpty();
    }
}

