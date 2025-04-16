package com.ai.balancelab_be.domain.member.controller;

import com.ai.balancelab_be.domain.member.dto.MemberDto;
import com.ai.balancelab_be.domain.member.dto.MemberInfoDto;
import com.ai.balancelab_be.domain.member.dto.MemberUpdateDto;
import com.ai.balancelab_be.domain.member.service.MemberService;
import com.ai.balancelab_be.domain.member.repository.MemberRepository;
import com.ai.balancelab_be.global.security.TokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.index.qual.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import com.ai.balancelab_be.global.security.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    @PostMapping("/fetchUserInfo")
    public ResponseEntity<MemberDto> fetchUserInfo(Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = (String) oAuth2User.getAttributes().get("email");


        // 예시: 실제 DB에서 사용자 정보 가져오기
        // UserEntity user = userRepository.findByUsername(username);
        // return ResponseEntity.ok(new MemberDto(user));

        // 임시 응답
        MemberDto member = MemberDto.builder()
                .email(email)
                .accessToken("token")
                .type("LOGIN_SUCCESS")
                .refreshToken("refresh")
                .build();


        return ResponseEntity.ok(member);
    }

    @GetMapping("/getMemberById")
    public ResponseEntity<MemberInfoDto> getMemberById(@PathVariable @Positive Long id) {
        MemberInfoDto memberInfoDto = memberService.findById(id);
        return ResponseEntity.ok(memberInfoDto);
    }

    @GetMapping("/info")
    public ResponseEntity<MemberInfoDto> getMemberInfo(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long memberId = userDetails.getMemberId();
            return ResponseEntity.ok(memberService.findById(memberId));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping(value = "/info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MemberInfoDto> updateMemberInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestPart("dto") MemberUpdateDto updateDto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // profileImage을 DTO에 포함했으므로, 여기서 설정
        updateDto.setProfileImage(profileImage);
        MemberInfoDto updated = memberService.updateMember(userDetails.getMemberId(), updateDto);
        return ResponseEntity.ok(updated);
    }
}
