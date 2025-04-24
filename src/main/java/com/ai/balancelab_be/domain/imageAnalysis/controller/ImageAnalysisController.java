package com.ai.balancelab_be.domain.imageAnalysis.controller;

import com.ai.balancelab_be.domain.imageAnalysis.service.ImageAnalysisService;
import com.ai.balancelab_be.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image-analysis")
public class ImageAnalysisController {

    private final ImageAnalysisService imageAnalysisService;

    @Value("${app.upload.dir}")
    private String uploadDir; // application.yml에서 주입받은 경로

    @PostMapping(value = "/start", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> start(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 허용된 파일 형식 확인 (ContentType 및 확장자)
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        if (!isValidImageType(contentType) || !isValidFileExtension(originalFilename)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "허용된 방식이 아닙니다. (허용 형식: image/png, image/jpeg, image/jpg)");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            // 업로드 디렉토리 생성 (없을 경우)
            Path homePath = Paths.get(System.getProperty("user.home"));
            Path uploadPath = homePath.resolve(uploadDir); // 홈 디렉토리 내 uploads 폴더
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일 저장
            String fileName = System.currentTimeMillis() + "_" + originalFilename;
            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath.toFile());

            // 이미지 분석 서비스 호출
            Map<String, Object> analysisResult = imageAnalysisService.analyzeDiet(userDetails.getMemberId(), filePath.toString());

            return ResponseEntity.ok(analysisResult);
        } catch (IOException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "파일 업로드 중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // 허용된 이미지 형식 확인 메서드 (ContentType)
    private boolean isValidImageType(String contentType) {
        return contentType != null && (
                contentType.equals("image/png") ||
                        contentType.equals("image/jpeg") ||
                        contentType.equals("image/jpg")
        );
    }

    // 허용된 파일 확장자 확인 메서드
    private boolean isValidFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return false;
        }
        String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        return extension.equals(".png") || extension.equals(".jpeg") || extension.equals(".jpg");
    }
}
