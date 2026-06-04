package com.example.billingservice.service;

import com.example.billingservice.event.ApiRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final UsageTrackingService usageTrackingService;

    @KafkaListener(topics = "api-requests", groupId = "billing-group")
    public void consume(ApiRequestEvent event) {
        if (event == null || event.getOrganizationId() == null) {
            log.warn("Received null or invalid API request event, skipping");
            return;
        }

        try {
            usageTrackingService.trackRequest(event.getOrganizationId());
            log.debug("Usage tracked: orgId={}, path={}, status={}",
                    event.getOrganizationId(), event.getPath(), event.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to track usage: orgId={}, error={}",
                    event.getOrganizationId(), e.getMessage());
        }
    }
}