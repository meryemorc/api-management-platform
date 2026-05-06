package com.example.apigateway.service;

import com.example.apigateway.event.ApiRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private static final String TOPIC = "api-requests";

    private final KafkaTemplate<String, ApiRequestEvent> kafkaTemplate;

    public void sendApiRequestEvent(ApiRequestEvent event) {
        kafkaTemplate.send(TOPIC, event.getOrganizationId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Event gönderilemedi: {}", ex.getMessage());
                    } else {
                        log.debug("Event gönderildi: topic={}, partition={}",
                                TOPIC, result.getRecordMetadata().partition());
                    }
                });
    }
}