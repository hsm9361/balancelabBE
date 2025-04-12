package com.ai.balancelab_be.domain.healthPrediction.controller;

import com.ai.balancelab_be.domain.healthPrediction.dto.memberDTO;
import com.ai.balancelab_be.domain.healthPrediction.service.MemberService;
import com.ai.balancelab_be.domain.healthPrediction.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class HealthMemberController {

    private final MemberService memberService;

    @GetMapping("/info")
    public ResponseEntity<memberDTO> getMemberInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            memberDTO memberInfo = memberService.getMemberInfo(email);
            return ResponseEntity.ok(memberInfo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();  // Member가 없을 경우 404 반환
        }
    }
}
