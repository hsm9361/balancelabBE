package com.ai.balancelab_be.domain.member.service;

import com.ai.balancelab_be.domain.member.dto.MemberDto;
import com.ai.balancelab_be.domain.member.dto.MemberInfoDto;
import com.ai.balancelab_be.domain.member.dto.MemberUpdateDto;
import com.ai.balancelab_be.domain.member.entity.GoalNutritionEntity;
import com.ai.balancelab_be.domain.member.entity.MemberEntity;
import com.ai.balancelab_be.domain.member.entity.WeightHistoryEntity;
import com.ai.balancelab_be.domain.member.repository.GoalNutritionRepository;
import com.ai.balancelab_be.domain.member.repository.MemberRepository;
import com.ai.balancelab_be.domain.member.repository.WeightHistoryRepository;
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
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final WeightHistoryRepository weightHistoryRepository;
    private final MemberRepository memberRepository;
    private final GoalNutritionRepository goalNutritionRepository;

    @Value("${app.upload.dir}")
    private String uploadDir; // application.yml에서 주입받은 경로

    @Value("${app.profiles-dir}")
    private String profilesDir;

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
                .membername(member.getMembername())
                .email(member.getEmail())
                .sub(member.getSub())
                .age(member.getAge())
                .height(member.getHeight())
                .weight(member.getWeight())
                .gender(member.getGender())
                .goalWeight(member.getGoalWeight())
                .activityLevel(member.getActivityLevel())
                .profileImageUrl(member.getProfileImageUrl())
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
        if (updateDto.getMembername() != null) member.setMembername(updateDto.getMembername());
        if (updateDto.getAge() != null) member.setAge(updateDto.getAge());
        if (updateDto.getHeight() != null) member.setHeight(updateDto.getHeight());

        // 체중 변경시 WeightHistory 기록
        if (updateDto.getWeight() != null && !updateDto.getWeight().equals(member.getWeight())) {
            double newWeight = updateDto.getWeight();

            // 몸무게 업데이트
            member.setWeight(newWeight);

            // 히스토리 저장
            WeightHistoryEntity weightHistory = WeightHistoryEntity.builder()
                    .member(member)
                    .weight(newWeight)
                    .build();
            weightHistoryRepository.save(weightHistory);
        }

        if (updateDto.getGender() != null) member.setGender(updateDto.getGender());
        if (updateDto.getActivityLevel() != null) member.setActivityLevel(updateDto.getActivityLevel());
        if (updateDto.getGoalWeight() != null) member.setGoalWeight(updateDto.getGoalWeight());

        memberRepository.save(member);

        if (updateDto.getGender() != null &&
                updateDto.getWeight() != null &&
                updateDto.getHeight() != null &&
                updateDto.getAge() != null &&
                updateDto.getActivityLevel() != null) {

            double weight = updateDto.getWeight();
            double height = updateDto.getHeight();
            int age = updateDto.getAge();
            String gender = updateDto.getGender();
            String activityLevel = updateDto.getActivityLevel();

            Double tdee = calculateTdee(weight, height, age, gender, activityLevel);

            if (tdee != null && updateDto.getGoalWeight() != null) {
                double currentWeight = weight;
                double goalWeight = updateDto.getGoalWeight();
                double weightDiff = currentWeight - goalWeight;

                // 하루 칼로리 차감량 계산
                double totalDeficit = weightDiff * 7700; // 1kg 감량 = 약 7700kcal
                double dailyDeficit = totalDeficit / updateDto.getGoalPeriod(); // 6개월 기준

                // 증량인 경우 (goalWeight > currentWeight) => dailyDeficit 음수됨
                double goalCalories = tdee - dailyDeficit;

                double carbRatio = updateDto.getCarbRatio();  // 사용자 입력: 탄수화물 비율
                double proteinRatio = updateDto.getProteinRatio();  // 사용자 입력: 단백질 비율
                double fatRatio = updateDto.getFatRatio();  // 사용자 입력: 지방 비율

                // 단백질: 체중 * 2g, 지방: 체중 * 1g
                double goalProtein = (goalCalories * proteinRatio) / 100 / 4;  // 1g 단백질 = 4kcal
                double goalFat = (goalCalories * fatRatio) / 100 / 9;  // 1g 지방 = 9kcal
                double remainingCalories = goalCalories - (goalProtein * 4 + goalFat * 9);
                double goalCarbo = remainingCalories / 4;  // 1g 탄수화물 = 4kcal

                GoalNutritionEntity goal = goalNutritionRepository.findByMember(member).orElse(null);

                if (goal == null) {
                    goal = GoalNutritionEntity.builder()
                            .member(member)
                            .tdeeCalories(tdee)
                            .goalCalories(goalCalories)
                            .goalCarbo(goalCarbo)
                            .goalProtein(goalProtein)
                            .goalFat(goalFat)
                            .build();
                } else {
                    goal.setTdeeCalories(tdee);
                    goal.setGoalCalories(goalCalories);
                    goal.setGoalCarbo(goalCarbo);
                    goal.setGoalProtein(goalProtein);
                    goal.setGoalFat(goalFat);
                }

                goalNutritionRepository.save(goal);
            }
        }


        return MemberInfoDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .age(member.getAge())
                .height(member.getHeight())
                .weight(member.getWeight())
                .gender(member.getGender())
                .profileImageUrl(member.getProfileImageUrl())
                .activityLevel(member.getActivityLevel())
                .goalWeight(member.getGoalWeight())
                .build();
    }

    // Tdee 계산하는 메서드 추가
    private Double calculateTdee(double weight, double height, int age, String gender, String activityLevel) {
        double bmr;

        if ("male".equalsIgnoreCase(gender)) {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5;
        } else if ("female".equalsIgnoreCase(gender)) {
            bmr = (10 * weight) + (6.25 * height) - (5 * age) - 161;
        } else {
            return null;
        }

        Map<String, Double> activityFactors = Map.of(
                "sedentary", 1.2,
                "lightly_active", 1.375,
                "moderately_active", 1.55,
                "very_active", 1.725,
                "extra_active", 1.9
        );

        Double factor = activityFactors.get(activityLevel.toLowerCase());
        if (factor == null) return null;

        return bmr * factor;
    }

}
