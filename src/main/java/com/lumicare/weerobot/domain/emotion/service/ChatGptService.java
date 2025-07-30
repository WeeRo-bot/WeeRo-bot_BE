package com.lumicare.weerobot.domain.emotion.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatGptService {

    private static final String API_KEY = ""; // OpenAI API 키
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public String getAdviceByEmotion(String topEmotion) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);

        String prompt = "당신은 감정 분석 결과에 따른 심리 상담사입니다. 사용자의 감정은 '" + topEmotion + "'입니다. 위로가 되는 짧고 따뜻한 메시지를 작성해주세요.";

        String requestBody = """
            {
              "model": "gpt-4",
              "messages": [
                {"role": "system", "content": "당신은 공감 능력이 뛰어난 심리상담사입니다."},
                {"role": "user", "content": "%s"}
              ],
              "temperature": 0.7
            }
            """.formatted(prompt);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
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
