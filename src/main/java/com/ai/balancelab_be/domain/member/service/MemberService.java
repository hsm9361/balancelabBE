package com.ai.balancelab_be.domain.member.service;

import com.ai.balancelab_be.domain.member.dto.MemberDto;
import com.ai.balancelab_be.domain.member.dto.MemberInfoDto;
import com.ai.balancelab_be.domain.member.dto.MemberUpdateDto;
import com.ai.balancelab_be.domain.member.entity.MemberEntity;
import com.ai.balancelab_be.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    @Value("${app.upload.dir}")
    private String uploadDir; // application.yml에서 주입받은 경로
    // 이메일을 통해 회원 정보 조회
    public MemberEntity getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public MemberDto getMemberInfo(String email) {
        MemberEntity memberEntity = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with email: " + email));

        MemberDto dto = new MemberDto();
        dto.setAge(memberEntity.getAge());
        dto.setHeight(memberEntity.getHeight());
        dto.setWeight(memberEntity.getWeight());
        dto.setGender(memberEntity.getGender());

        return dto;
    }

    public MemberInfoDto findById(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid member ID");
        }

        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));

        return MemberInfoDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .sub(member.getSub())
                .age(member.getAge())
                .height(member.getHeight())
                .weight(member.getWeight())
                .gender(member.getGender())
                .build();
    }

    private String saveProfileImage(MultipartFile profileImage) {
        if (profileImage == null || profileImage.isEmpty()) {
            return null;
        }

        try {
            // 기본 업로드 디렉토리 설정
            Path uploadPath = Paths.get(System.getProperty("user.home"), uploadDir, "profiles");
            
            // 디렉토리가 없으면 생성
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일명 생성 (중복 방지)
            String originalFilename = profileImage.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + extension;

            // 파일 저장
            Path filePath = uploadPath.resolve(fileName);
            profileImage.transferTo(filePath.toFile());

            // 상대 경로로 저장 (DB에는 profiles/파일명 형태로 저장)
            return "profiles/" + fileName;

        } catch (IOException e) {
            log.error("Failed to save profile image", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload image");
        }
    }

    @Transactional
    public MemberInfoDto updateMember(Long memberId, MemberUpdateDto updateDto) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));

        // 프로필 이미지 처리
        MultipartFile profileImage = updateDto.getProfileImage();
        if (profileImage != null && !profileImage.isEmpty()) {
            String savedImagePath = saveProfileImage(profileImage);
            member.setProfileImageUrl(savedImagePath);
        }

        // 나머지 필드 업데이트
        if (updateDto.getUsername() != null) member.setUsername(updateDto.getUsername());
        if (updateDto.getAge() != null) member.setAge(updateDto.getAge());
        if (updateDto.getHeight() != null) member.setHeight(updateDto.getHeight());
        if (updateDto.getWeight() != null) member.setWeight(updateDto.getWeight());
        if (updateDto.getGender() != null) member.setGender(updateDto.getGender());

        memberRepository.save(member);

        return MemberInfoDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .age(member.getAge())
                .height(member.getHeight())
                .weight(member.getWeight())
                .gender(member.getGender())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
