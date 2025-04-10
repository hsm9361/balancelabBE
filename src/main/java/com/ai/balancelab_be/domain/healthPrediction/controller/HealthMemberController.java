package com.ai.balancelab_be.domain.healthPrediction.controller;

import com.ai.balancelab_be.domain.healthPrediction.dto.memberDTO;
import com.ai.balancelab_be.domain.healthPrediction.service.MemberService;
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
        Long memberId = Long.parseLong(authentication.getName());
        
        memberDTO memberInfo = memberService.getMemberInfo(memberId);
        return ResponseEntity.ok(memberInfo);
    }
} 