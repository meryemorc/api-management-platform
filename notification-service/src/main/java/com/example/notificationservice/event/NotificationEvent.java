package com.example.notificationservice.event;

import com.example.notificationservice.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private UUID organizationId;
    private String recipientEmail;
    private String recipientName;
    private NotificationType notificationType;
    private Map<String, Object> metadata;
}