package com.lumicare.weerobot.domain.emotion.service;

import com.lumicare.weerobot.domain.emotion.dto.*;
import com.lumicare.weerobot.domain.emotion.entity.EmotionEntity;
import com.lumicare.weerobot.domain.emotion.enums.EmotionType;
import com.lumicare.weerobot.domain.emotion.repository.EmotionRepository;
import com.lumicare.weerobot.domain.emotion.util.CalendarDateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.lumicare.weerobot.domain.emotion.entity.UserEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmotionService {

    private final EmotionRepository emotionRepository;
    private final PiClient piClient;
    private final GptService gptService;

    // [ RPI에 감정 분석 API 호출 -> 분석 결과 반환 ]
    @Transactional
    public EmotionResultDto analyzeEmotion() {
        // 라즈베리파이 API 호출
        PiResponseDto pi = piClient.analyze();

        // 오류가 있으면 저장하지 않고 바로 반환
        if (pi.getError() != null) {
            log.warn("[EmotionService] Pi 오류 응답: {}", pi.getError());
            return EmotionResultDto.builder()
                    .emotion(pi.getEmotion())
                    .confidence(pi.getConfidence())
                    .advice(pi.getAdvice())
                    .error(pi.getError())
                    .build();
        }

        // Entity 생성 및 저장
        EmotionEntity emotion = EmotionEntity.builder()
                .emotion(pi.getEmotion())
                .confidence(pi.getConfidence())
                .build();

        EmotionEntity saved = emotionRepository.save(emotion);

        // EmotionResultDto 반환
        return EmotionResultDto.builder()
                .id(saved.getEmotionId())
                .emotion(saved.getEmotion())
                .confidence(saved.getConfidence())
                .capturedAt(saved.getCapturedAt())
                .advice(pi.getAdvice())
                .error(pi.getError())
                .build();
    }

    // 한달 계산
    private CalendarDateUtil getMonthDateRange(int year, int month) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.withDayOfMonth(start.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);

        return new CalendarDateUtil(start, end);
    }

    // 한달 감정 조회
    public List<CalendarMonthEmotionResponseDto> getMonthEmotion(int year, int month) {
        CalendarDateUtil date = getMonthDateRange(year, month);

        // 한달 감정 분석 데이터 저장
        List<EmotionEntity> monthEmotion = emotionRepository.findByCapturedAtBetweenOrderByCapturedAtAsc(date.getStart(), date.getEnd());

        // 날짜 형식
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return monthEmotion.stream()
                .map(emotion -> CalendarMonthEmotionResponseDto.builder()
                        .emotion_id(emotion.getEmotionId())
                        .day(emotion.getCapturedAt().format(formatter))  // 날짜 문자열
                        .mainEmotion(emotion.getEmotion())
                        .build())
                .collect(Collectors.toList());
    }

    // 감정 개수 계산
    private Map<EmotionType, Long> getEmotionCountResult(List<EmotionEntity> emotion) {
        return emotion.stream().collect(Collectors.groupingBy(
                EmotionEntity::getEmotion,
                Collectors.counting()
        ));
    }

    // 감정별 횟수 확인
    public List<EmotionCountResponseDto> getEmotionCount(int year, int month) {
        CalendarDateUtil date = getMonthDateRange(year, month);

        // 한달 감정 분석 데이터 저장
        List<EmotionEntity> monthEmotion = emotionRepository.findByCapturedAtBetween(date.getStart(), date.getEnd());

        // 감정별 횟수 계산
        Map<EmotionType, Long> countResult = getEmotionCountResult(monthEmotion);

        return countResult.entrySet().stream().map(emotion ->
                new EmotionCountResponseDto(
                        emotion.getKey(),
                        emotion.getValue().intValue()
                )
        ).collect(Collectors.toList());
    }

    // 솔루션 제공
    public EmotionSolutionDto getEmotionSolution(int year, int month) {

        // JWT에서 현재 사용자 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return new EmotionSolutionDto(null, "로그인 정보가 없습니다.");
        }

        UserEntity currentUser = (UserEntity) authentication.getPrincipal(); // JWT에서 UserEntity가 담겨 있다고 가정

        List<EmotionCountResponseDto> countList = getEmotionCount(year, month);

        // 우선순위 정의
        List<EmotionType> priority = List.of(
                EmotionType.SAD,
                EmotionType.ANGRY,
                EmotionType.FEAR,
                EmotionType.DISGUST,
                EmotionType.SURPRISE,
                EmotionType.NEUTRAL,
                EmotionType.HAPPY
        );

        // 가장 많은 감정 찾기
        EmotionType mainEmotion = countList.stream()
                .sorted((a, b) -> {
                    int cmp = Integer.compare(b.getCount(), a.getCount()); // 내림차순
                    if (cmp != 0) return cmp;
                    return Integer.compare(
                            priority.indexOf(a.getEmotionType()),
                            priority.indexOf(b.getEmotionType())
                    );
                })
                .map(EmotionCountResponseDto::getEmotionType)
                .findFirst()
                .orElse(null);

        log.info("감정 확인 " + mainEmotion);

        if (mainEmotion == null) {
            return new EmotionSolutionDto(null, "감정 데이터가 없습니다.");
        }

        // GPT로 솔루션 메시지 생성
        String solutionMessage = gptService.generateSolutionMessage(currentUser,mainEmotion);

        return new EmotionSolutionDto(mainEmotion, solutionMessage);
    }

    // 하루 날짜 계산
    private CalendarDateUtil getDayDateRange(int year, int month, int day) {
        LocalDateTime start = LocalDateTime.of(year, month, day, 0, 0);
        LocalDateTime end = LocalDateTime.of(year, month, day, 23, 59, 59);

        return new CalendarDateUtil(start, end);
    }

    // 하루 감정 조회
    public List<CalendarDayEmotionResponseDto> getDayEmotion(int year, int month, int day) {
        CalendarDateUtil date = getDayDateRange(year, month, day);

        // 하루 감정 분석 데이터 저장
        List<EmotionEntity> dayEmotion = emotionRepository.findByCapturedAtBetween(date.getStart(), date.getEnd());

        List<TimeEmotionDto> timeEmotions = dayEmotion.stream()
                .map(emotion -> TimeEmotionDto.builder()
                        .time(emotion.getCapturedAt().toLocalTime().toString())
                        .emotion(emotion.getEmotion())
                        .build())
                .collect(Collectors.toList());

        String formattedDay = LocalDate.of(year, month, day).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 메인 감정
        EmotionType mainEmotion = dayEmotion.stream()
                .collect(Collectors.groupingBy(EmotionEntity::getEmotion, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        return dayEmotion.stream()
                .map(emotion -> CalendarDayEmotionResponseDto.builder()
                        .day(formattedDay)
                        .mainEmotion(mainEmotion)
                        .timeEmotions(timeEmotions)
                        .build())
                .collect(Collectors.toList());
    }
}