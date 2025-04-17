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

        try {
            // 업로드 디렉토리 생성 (없을 경우)
            Path homePath = Paths.get(System.getProperty("user.home"));
            Path uploadPath = homePath.resolve(uploadDir); // 홈 디렉토리 내 uploads 폴더
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일 저장
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath.toFile());

            // 이미지 분석 서비스 호출
            Map<String, Object> analysisResult = imageAnalysisService.analyzeDiet(userDetails.getMemberId(), filePath.toString());

            return ResponseEntity.ok(analysisResult);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
