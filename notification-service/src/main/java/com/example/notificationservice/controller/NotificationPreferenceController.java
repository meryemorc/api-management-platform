package com.example.notificationservice.controller;

import com.example.notificationservice.dto.request.UpdatePreferenceRequest;
import com.example.notificationservice.dto.response.NotificationPreferenceResponse;
import com.example.notificationservice.entity.NotificationPreference;
import com.example.notificationservice.service.NotificationPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationPreferenceController {

    private final NotificationPreferenceService preferenceService;

    @GetMapping("/{organizationId}/preferences")
    public ResponseEntity<NotificationPreferenceResponse> getPreferences(
            @PathVariable UUID organizationId) {

        NotificationPreference pref = preferenceService.getOrCreate(organizationId);
        return ResponseEntity.ok(toResponse(pref));
    }

    @PutMapping("/{organizationId}/preferences")
    public ResponseEntity<NotificationPreferenceResponse> updatePreferences(
            @PathVariable UUID organizationId,
            @RequestBody UpdatePreferenceRequest request) {

        NotificationPreference updated = NotificationPreference.builder()
                .emailEnabled(request.getEmailEnabled())
                .webhookEnabled(request.getWebhookEnabled())
                .rateLimitWarningEnabled(request.getRateLimitWarningEnabled())
                .rateLimitExceededEnabled(request.getRateLimitExceededEnabled())
                .apiKeyExpiringEnabled(request.getApiKeyExpiringEnabled())
                .dailyReportEnabled(request.getDailyReportEnabled())
                .highErrorRateEnabled(request.getHighErrorRateEnabled())
                .build();

        NotificationPreference saved = preferenceService.update(organizationId, updated);
        return ResponseEntity.ok(toResponse(saved));
    }

    private NotificationPreferenceResponse toResponse(NotificationPreference pref) {
        return NotificationPreferenceResponse.builder()
                .id(pref.getId())
                .organizationId(pref.getOrganizationId())
                .emailEnabled(pref.getEmailEnabled())
                .webhookEnabled(pref.getWebhookEnabled())
                .rateLimitWarningEnabled(pref.getRateLimitWarningEnabled())
                .rateLimitExceededEnabled(pref.getRateLimitExceededEnabled())
                .apiKeyExpiringEnabled(pref.getApiKeyExpiringEnabled())
                .dailyReportEnabled(pref.getDailyReportEnabled())
                .highErrorRateEnabled(pref.getHighErrorRateEnabled())
                .build();
    }
}