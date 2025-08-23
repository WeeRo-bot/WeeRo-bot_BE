package com.lumicare.weerobot.domain.emotion.service;

import com.lumicare.weerobot.domain.emotion.entity.UserEntity;
import com.lumicare.weerobot.domain.emotion.enums.EmotionType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GptService {

    private final RestTemplate restTemplate;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public String generateSolutionMessage(UserEntity user, EmotionType emotionType) {
        // 사용자 정보 문자열 구성
        String userInfo = String.format(
                "이름: %s, 나이: %d, 성별: %s, 직업: %s, 고민: %s",
                user.getName(),
                user.getAge(),
                user.getGender() != null ? user.getGender().toString() : "알 수 없음",
                user.getOccupation() != null ? user.getOccupation() : "알 수 없음",
                user.getConcern() != null ? user.getConcern() : "알 수 없음"
        );

        // 감정별 프롬프트 + 사용자 정보
        String prompt = String.format(
                "사용자 정보: [%s]\n%s",
                userInfo,
                getPromptForEmotion(emotionType)
        );

        System.out.println("GPT 요청 프롬프트: " + prompt);

        Map<String, Object> request = Map.of(
                "model", "gpt-4",
                "messages", List.of(
                        Map.of("role", "system", "content", "너는 사용자 정보를 기반으로, 감정에 맞는 따뜻한 조언과 위로 메시지를 제공하는 상담사야."),
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_API_URL, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<?> choices = (List<?>) response.getBody().get("choices");
                if (!choices.isEmpty()) {
                    Map<?, ?> choice = (Map<?, ?>) choices.get(0);
                    Map<?, ?> message = (Map<?, ?>) choice.get("message");
                    return message.get("content").toString().trim();
                }
            }
            return "GPT로부터 메시지를 받지 못했습니다.";
        } catch (Exception e) {
            return "감정에 맞는 솔루션을 생성하지 못했습니다. 다시 시도해주세요.";
        }
    }

    private String getPromptForEmotion(EmotionType emotionType) {
        return switch (emotionType) {
            case HAPPY -> "사용자가 한 달 동안 주로 행복한 감정을 느꼈습니다. 그에 맞는 따뜻한 메시지와 긍정적인 흐름을 오래 유지하고 확장할 수 있도록 장기 습관을 3~4문장, 250자 이내로 작성해주세요. 이모지도 하나 넣어주세요.";
            case SAD -> "사용자가 한 달 동안 주로 슬픈 감정을 느꼈습니다. 위로가 되는 메시지와 마음을 보듬고 회복력을 높일 수 있도록 장기 습관을 3~4문장, 250자 이내로 작성해주세요. 이모지도 하나 넣어주세요.";
            case ANGRY -> "사용자가 한 달 동안 주로 화난 감정을 느꼈습니다. 진정될 수 있는 조언 메시지와 감정 조절과 에너지 전환을 초점을 둘 수 있도록 장기 습관을 3~4문장, 250자 이내로 작성해주세요. 이모지도 하나 넣어주세요.";
            case FEAR -> "사용자가 한 달 동안 주로 불안한 감정을 느꼈습니다. 마음을 안정시켜주는 메시지와 안정감을 키우고 예측 가능성을 높일 수 있는 장기 습관을 3~4문장, 250자 이내로 작성해주세요. 이모지도 하나 넣어주세요.";
            case DISGUST -> "사용자가 한 달 동안 주로 불쾌한 감정을 느꼈습니다. 기분 전환이 될 수 있는 메시지와 감각 리셋과 환경 조정에 초점을 두고 장기 습관을 3~4문장, 250자 이내로 작성해주세요. 이모지도 하나 넣어주세요.";
            case SURPRISE -> "사용자가 한 달 동안 놀라움을 많이 느꼈습니다. 긍정적인 메시지와 예측 불가능한 상황을 긍정적으로 활용할 수 있도록 장기 습관을 3~4문장, 250자 이내로 작성해주세요. 이모지도 하나 넣어주세요.";
            case NEUTRAL -> "사용자가 한 달 동안 평온한 감정을 주로 느꼈습니다. 차분함을 유지할 수 있는 메시지와 평정 상태를 유지하면서 활력과 의미를 더할 수 있도록 장기 습관을 3~4문장, 250자 이내로 작성해주세요. 이모지도 하나 넣어주세요.";
            default -> "사용자의 주요 감정을 특정하기 어렵습니다. 지난 30일을 돌아보며 기본 회복 루틴과 기본 일과 재정비 방법을 제안해 주세요. 3~4문장, 250자 이내로과 이모지도 하나 넣어주세요.";
        };
    }
}
