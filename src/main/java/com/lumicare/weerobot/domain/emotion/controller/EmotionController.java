package com.lumicare.weerobot.domain.emotion.controller;

import com.lumicare.weerobot.domain.emotion.dto.EmotionResultDto;
import com.lumicare.weerobot.domain.emotion.entity.EmotionEntity;
import com.lumicare.weerobot.domain.emotion.service.EmotionService;
import com.lumicare.weerobot.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emotions")
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionService emotionService;

    // RPI 감정 분석 → DB 감정 등록
    @PostMapping("/analyze")
    public ResponseEntity<?> captureEmotion() {
        EmotionEntity saved = emotionService.analyzeEmotion();

        return ResponseEntity.ok(ApiResponse.success("SUCCESS_ANALYZE_EMOTION", "성공적으로 감정을 분석 및 저장했습니다.", saved));
    }

}
