package com.example.notificationservice.service;

import com.example.notificationservice.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final NotificationService notificationService;

    @KafkaListener(topics = "notification-events", groupId = "notification-group")
    public void consume(NotificationEvent event) {
        if (event == null) {
            log.warn("Received null notification event, skipping");
            return;
        }

        log.info("Notification event received: type={}, org={}, recipient={}",
                event.getNotificationType(),
                event.getOrganizationId(),
                event.getRecipientEmail());

        try {
            notificationService.processNotification(event);
        } catch (Exception e) {
            log.error("Failed to process notification event: type={}, org={}, error={}",
                    event.getNotificationType(), event.getOrganizationId(), e.getMessage());
        }
    }
}