package com.example.notificationservice.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class WebhookSubscriptionResponse {
    private UUID id;
    private UUID organizationId;
    private String url;
    private Boolean isActive;
    private String eventTypes;
    private LocalDateTime createdAt;
}