package com.example.notificationservice.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdatePreferenceRequest {
    private Boolean emailEnabled;
    private Boolean webhookEnabled;
    private Boolean rateLimitWarningEnabled;
    private Boolean rateLimitExceededEnabled;
    private Boolean apiKeyExpiringEnabled;
    private Boolean dailyReportEnabled;
    private Boolean highErrorRateEnabled;
}