package com.firstlogistics.notificationservice.infrastructure.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.firstlogistics.notificationservice.domain.client.NotificationClient;
import com.firstlogistics.notificationservice.domain.enums.MessengerType;
import com.firstlogistics.notificationservice.domain.exception.NotificationErrorCode;
import com.firstlogistics.notificationservice.domain.exception.NotificationException;
import com.firstlogistics.notificationservice.infrastructure.config.SlackProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
public class SlackNotificationClient implements NotificationClient {

    private final SlackProperties slackProperties;
    private final RestClient restClient;

    public SlackNotificationClient(SlackProperties slackProperties) {
        this.slackProperties = slackProperties;

        this.restClient = RestClient.create("https://slack.com/api");
    }

    @Override
    public void send(String slackId, String content) {
        log.info("[SLACK API CALL] To: {}, Content: {}", slackId, content);

        String token = slackProperties.getToken();

        try {
            // Slack API 호출 (POST /chat.postMessage)
            ResponseEntity<JsonNode> response = restClient.post()
                    .uri("/chat.postMessage")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "channel", slackId, // 수신자 슬랙 ID (U...)
                            "text", content      // 보낼 메시지 내용
                    ))
                    .retrieve()
                    .toEntity(JsonNode.class);

            JsonNode body = response.getBody();

            // 슬랙 API 응답값이 "ok": true 인지 확인
            if (body == null || !body.path("ok").asBoolean()) {
                String error = (body != null) ? body.path("error").asText() : "응답 바디 없음";
                log.error("슬랙 메시지 전송 실패: {}", error);
                throw new NotificationException(NotificationErrorCode.SLACK_SEND_FAILED);
            }

            log.info("슬랙 메시지 전송 성공: {}", slackId);

        } catch (Exception e) {
            log.error("슬랙 API 통신 중 예외 발생: {}", e.getMessage());
            throw new NotificationException(NotificationErrorCode.SLACK_API_COMMUNICATION_FAILED);
        }
    }

    @Override
    public boolean support(MessengerType type) {
        return MessengerType.SLACK.equals(type);
    }
}
