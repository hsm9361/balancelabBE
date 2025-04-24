package com.ai.balancelab_be.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateDto {
    private String username;

    @Positive(message = "Age must be positive")
    private Integer age;

    @Positive(message = "Height must be positive")
    private Double height;

    @Positive(message = "Weight must be positive")
    private Double weight;

    @Size(max = 10, message = "Gender must be less than 10 characters")
    private String gender;

    private String membername;

    private LocalDate birthDate;

    //목표 몸무게
    private Double goalWeight;

    //활동량
    private String activityLevel;

    // profileImageUrl → MultipartFile
    private MultipartFile profileImage;
}
