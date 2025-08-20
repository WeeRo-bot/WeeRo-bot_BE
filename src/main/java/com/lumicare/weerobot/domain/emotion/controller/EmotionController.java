package com.lumicare.weerobot.domain.emotion.controller;

import com.lumicare.weerobot.domain.emotion.dto.*;
import com.lumicare.weerobot.domain.emotion.entity.EmotionEntity;
import com.lumicare.weerobot.domain.emotion.service.EmotionService;
import com.lumicare.weerobot.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/emotions")
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionService emotionService;

    // RPI 감정 분석 → DB 감정 등록
    @PostMapping("/analyze")
    public ResponseEntity<?> captureEmotion() {
        EmotionResultDto saved = emotionService.analyzeEmotion();

        return ResponseEntity.ok(ApiResponse.success("SUCCESS_ANALYZE_EMOTION", "성공적으로 감정을 분석 및 저장했습니다.", saved));
    }

    // 한달 감정 조회
    @GetMapping("/calendar")
    public ResponseEntity<?> getMonthEmotion(@RequestParam int year, @RequestParam int month) {
        List<CalendarMonthEmotionResponseDto> result = emotionService.getMonthEmotion(year, month);

        return ResponseEntity.ok(ApiResponse.success("SUCCESS_GET_MONTH_EMOTION", "한달 감정 결과를 성공적으로 조회했습니다.", result));
    }

    // 한달 감정 횟수 계산
    @GetMapping("/count")
    public ResponseEntity<?> getMonthEmotionCount(@RequestParam int year, @RequestParam int month) {
        List<EmotionCountResponseDto> result = emotionService.getEmotionCount(year, month);

        return ResponseEntity.ok(ApiResponse.success("SUCCESS_GET_MONTH_EMOTION_COUNT", "감정별 횟수를 성공적으로 가져왔습니다", result));
    }

    // 솔루션 제공
    @GetMapping("/solution")
    public ResponseEntity<?> getSolution(@RequestParam int year, @RequestParam int month) {
        EmotionSolutionDto result = emotionService.getEmotionSolution(year, month);

        return ResponseEntity.ok(ApiResponse.success("SUCCESS_GET_SOLUTION", "솔루션을 성공적으로 조회했습니다.", result));
    }

    // 하루 감정 조회
    @GetMapping("/day")
    public ResponseEntity<?> getDayEmotion(@RequestParam int year, @RequestParam int month, @RequestParam int day) {
        List<CalendarDayEmotionResponseDto> result = emotionService.getDayEmotion(year, month, day);
        log.info(result.toString());
        return ResponseEntity.ok(ApiResponse.success("SUCCESS_GET_MONTH_EMOTION", "하루 감정 결과를 성공적으로 조회했습니다.", result));
    }
}
