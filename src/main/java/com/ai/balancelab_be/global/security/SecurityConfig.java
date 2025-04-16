package com.ai.balancelab_be.global.security;

import com.ai.balancelab_be.domain.auth.handler.OAuth2SuccessHandler;
import com.ai.balancelab_be.domain.member.dto.MemberDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    public SecurityConfig(TokenProvider tokenProvider, ObjectMapper objectMapper, OAuth2SuccessHandler oAuth2SuccessHandler) {
        this.tokenProvider = tokenProvider;
        this.objectMapper = objectMapper;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/oauth2/**", "/auth/**", "/diet-analysis/**","/image-analysis/**","/member/**","/api/health-prediction/**","/api/diet/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(oidcUserService())
                        )
                        .successHandler(oAuth2SuccessHandler) // ← 요거로 변경
                        .failureHandler((request, response, exception) -> {
                            MemberDto errorResponse = MemberDto.builder()
                                    .type("LOGIN_FAILURE")
                                    .build();
                            
                            String errorJson = objectMapper.writeValueAsString(errorResponse);
                            String encodedError = URLEncoder.encode(errorJson, StandardCharsets.UTF_8.toString());
                            
                            response.sendRedirect("http://localhost:3000/oauth/callback?error=" + encodedError);
                        })
                )
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private OidcUserService oidcUserService() {
        return new OidcUserService();
    }
}