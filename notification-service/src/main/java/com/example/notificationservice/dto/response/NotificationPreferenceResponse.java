package com.example.notificationservice.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class NotificationPreferenceResponse {
    private UUID id;
    private UUID organizationId;
    private Boolean emailEnabled;
    private Boolean webhookEnabled;
    private Boolean rateLimitWarningEnabled;
    private Boolean rateLimitExceededEnabled;
    private Boolean apiKeyExpiringEnabled;
    private Boolean dailyReportEnabled;
    private Boolean highErrorRateEnabled;
}