package com.ai.balancelab_be.domain.imageAnalysis.controller;

import com.ai.balancelab_be.domain.imageAnalysis.service.ImageAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image-analysis")
public class ImageAnalysisController {

    private final ImageAnalysisService imageAnalysisService;

    @Value("${app.upload.dir}")
    private String uploadDir; // application.yml에서 주입받은 경로

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadAndAnalyzeImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId) {
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
//            String analysisResult = imageAnalysisService.analyzeDiet(userId, filePath.toString());

            return ResponseEntity.ok("Image uploaded and analyzed successfully: " + "ok");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to upload image: " + e.getMessage());
        }
    }
}
