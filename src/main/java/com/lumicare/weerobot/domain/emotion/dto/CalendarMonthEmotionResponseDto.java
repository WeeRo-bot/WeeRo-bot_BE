package com.lumicare.weerobot.domain.emotion.dto;

import com.lumicare.weerobot.domain.emotion.enums.EmotionType;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarMonthEmotionResponseDto {
    private Long emotion_id;
    private String day;
    private EmotionType mainEmotion;
}
