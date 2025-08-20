package com.lumicare.weerobot.domain.emotion.dto;

import com.lumicare.weerobot.domain.emotion.enums.EmotionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarDayEmotionResponseDto {
    private String day;
    private EmotionType mainEmotion;
    private List<TimeEmotionDto> timeEmotions;
}


