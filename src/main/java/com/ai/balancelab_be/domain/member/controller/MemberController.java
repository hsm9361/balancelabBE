package com.ai.balancelab_be.domain.member.controller;

import com.ai.balancelab_be.domain.imageAnalysis.service.ImageAnalysisService;
import com.ai.balancelab_be.domain.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    @PostMapping("/fetchUserInfo")
    public ResponseEntity<MemberDto> fetchUserInfo(Authentication authentication) {
        String username = authentication.getName();


        // 예시: 실제 DB에서 사용자 정보 가져오기
        // UserEntity user = userRepository.findByUsername(username);
        // return ResponseEntity.ok(new MemberDto(user));

        // 임시 응답
        MemberDto member = MemberDto.builder()
                .email(username)
                .accessToken("token")
                .type("LOGIN_SUCCESS")
                .refreshToken("refresh")
                .build();

        return ResponseEntity.ok(member);
    }
}
