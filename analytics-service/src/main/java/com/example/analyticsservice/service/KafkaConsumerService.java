package com.example.analyticsservice.service;

import com.example.analyticsservice.event.ApiRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final AnalyticsService analyticsService;

    @KafkaListener(topics = "api-requests", groupId = "analytics-group")
    public void consume(ApiRequestEvent event) {
        try {
            log.info("Event alındı: org={}, path={}", event.getOrganizationId(), event.getPath());
            analyticsService.saveRequest(event);
        } catch (Exception e) {
            log.error("Event işlenemedi: {}", e.getMessage());
        }
    }
}