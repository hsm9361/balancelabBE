package com.ai.balancelab_be.global.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private long accessTokenValidityInMilliseconds = 3600000; // 1시간
    private long refreshTokenValidityInMilliseconds = 2592000000L; // 30일

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessTokenValidityInMilliseconds() {
        return accessTokenValidityInMilliseconds;
    }

    public void setAccessTokenValidityInMilliseconds(long accessTokenValidityInMilliseconds) {
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
    }

    public long getRefreshTokenValidityInMilliseconds() {
        return refreshTokenValidityInMilliseconds;
    }

    public void setRefreshTokenValidityInMilliseconds(long refreshTokenValidityInMilliseconds) {
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
    }
} 