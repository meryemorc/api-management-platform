package com.example.notificationservice.dto.response;

import com.example.notificationservice.enums.NotificationChannel;
import com.example.notificationservice.enums.NotificationStatus;
import com.example.notificationservice.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class NotificationLogResponse {
    private UUID id;
    private UUID organizationId;
    private String recipientEmail;
    private NotificationType notificationType;
    private NotificationChannel channel;
    private NotificationStatus status;
    private String subject;
    private String errorMessage;
    private Integer retryCount;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}