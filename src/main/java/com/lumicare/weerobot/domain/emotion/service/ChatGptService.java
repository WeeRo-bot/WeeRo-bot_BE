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

    @Value("${openai.api-key}")
    private String apiKey;
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public String getAdviceByEmotion(UserEntity user, String topEmotion) {
        RestTemplate restTemplate = new RestTemplate();
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
                        "사용자의 상황과 감정을 고려해, 진심 어린 위로와 격려의 말을 1~2문장, 100자 이하로 작성해주세요.",
                userInfo,
                topEmotion
        );

        // JSON 객체 생성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4");
        requestBody.put("temperature", 0.7);

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "너는 사용자 정보를 기반으로 감정에 공감하며 따뜻한 위로를 주는 심리 상담사야.");
        messages.add(systemMessage);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);

        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            return "조언을 가져오는 중 오류가 발생했어요.";
        }
    }

}
