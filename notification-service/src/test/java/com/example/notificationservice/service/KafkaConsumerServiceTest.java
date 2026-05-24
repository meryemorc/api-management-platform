package com.example.notificationservice.service;

import com.example.notificationservice.event.NotificationEvent;
import com.example.notificationservice.enums.NotificationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerServiceTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @Test
    @DisplayName("Should process notification event when received from Kafka")
    void shouldProcessNotificationEvent() {
        // GIVEN
        NotificationEvent event = NotificationEvent.builder()
                .organizationId(UUID.randomUUID())
                .recipientEmail("test@test.com")
                .recipientName("Test User")
                .notificationType(NotificationType.RATE_LIMIT_WARNING)
                .metadata(Map.of("usagePercent", "85"))
                .build();

        // WHEN
        kafkaConsumerService.consume(event);

        // THEN
        verify(notificationService, times(1)).processNotification(eq(event));
    }

    @Test
    @DisplayName("Should not throw exception when notification processing fails")
    void shouldHandleProcessingException() {
        // GIVEN
        NotificationEvent event = NotificationEvent.builder()
                .organizationId(UUID.randomUUID())
                .recipientEmail("test@test.com")
                .recipientName("Test User")
                .notificationType(NotificationType.HIGH_ERROR_RATE)
                .metadata(Map.of())
                .build();

        doThrow(new RuntimeException("Processing failed"))
                .when(notificationService).processNotification(any());

        // WHEN & THEN - exception fırlatmamalı
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                kafkaConsumerService.consume(event));
    }

    @Test
    @DisplayName("Should handle null event gracefully")
    void shouldHandleNullEvent() {
        // WHEN & THEN
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                kafkaConsumerService.consume(null));

        verify(notificationService, never()).processNotification(any());
    }
}