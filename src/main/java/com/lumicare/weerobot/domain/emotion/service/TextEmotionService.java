package com.lumicare.weerobot.domain.emotion.service;
import com.lumicare.weerobot.domain.emotion.dto.TextRequest;
import com.lumicare.weerobot.domain.emotion.dto.TextEmotionResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Service
public class TextEmotionService {

    private final String FASTAPI_URL = "http://192.168.0.8:8001/predict";

    public TextEmotionResponse sendToFastAPI(TextRequest request) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TextRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<TextEmotionResponse> response = restTemplate.exchange(
                FASTAPI_URL,
                HttpMethod.POST,
                entity,
                TextEmotionResponse.class
        );

        return response.getBody();
    }
}

