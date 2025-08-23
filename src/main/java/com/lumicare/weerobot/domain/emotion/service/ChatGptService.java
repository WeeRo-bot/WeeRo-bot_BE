package com.lumicare.weerobot.domain.emotion.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumicare.weerobot.domain.emotion.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ChatGptService {

    private final RestTemplate restTemplate;

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    // RestTemplate 주입
    public ChatGptService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 사용자 정보 + 감정 기반 GPT 조언
    public String getAdviceByEmotion(UserEntity user, String topEmotion) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 사용자 정보 문자열로 조합
        String userInfo = String.format(
                "이름: %s, 나이: %d, 성별: %s, 직업: %s, 고민: %s",
                user.getName(),
                user.getAge(),
                user.getGender() != null ? user.getGender().toString() : "알 수 없음",
                user.getOccupation() != null ? user.getOccupation() : "알 수 없음",
                user.getConcern() != null ? user.getConcern() : "알 수 없음"
        );

        String prompt = String.format(
                "사용자 정보: [%s]. 현재 사용자의 감정은 '%s'입니다. " +
                        "사용자의 상황과 감정을 고려해, 감정에 대해 짧게 반응·조언을 1~2문장, 100자 이하로 작성해주세요.",
                userInfo,
                topEmotion
        );

        // 요청 JSON 구성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4");
        requestBody.put("temperature", 0.7);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMessage = Map.of(
                "role", "system",
                "content", "너는 사용자 정보를 기반으로 사용자에게 감정에 따라 따뜻한 위로 메시지를 주는 조언가야."
        );
        messages.add(systemMessage);

        Map<String, String> userMessage = Map.of(
                "role", "user",
                "content", prompt
        );
        messages.add(userMessage);

        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "조언을 가져오는 중 오류가 발생했어요.";
        }
    }
}

