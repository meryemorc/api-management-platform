package com.example.notificationservice.service;

import com.example.notificationservice.entity.NotificationPreference;
import com.example.notificationservice.exception.ResourceNotFoundException;
import com.example.notificationservice.repository.NotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;

    public NotificationPreference getOrCreate(UUID organizationId) {
        return preferenceRepository.findByOrganizationId(organizationId)
                .orElseGet(() -> {
                    NotificationPreference defaultPref = NotificationPreference.builder()
                            .organizationId(organizationId)
                            .emailEnabled(true)
                            .webhookEnabled(false)
                            .rateLimitWarningEnabled(true)
                            .rateLimitExceededEnabled(true)
                            .apiKeyExpiringEnabled(true)
                            .dailyReportEnabled(false)
                            .highErrorRateEnabled(true)
                            .build();
                    return preferenceRepository.save(defaultPref);
                });
    }

    public NotificationPreference update(UUID organizationId, NotificationPreference updated) {
        NotificationPreference existing = preferenceRepository
                .findByOrganizationId(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Preferences not found for org: " + organizationId));

        existing.setEmailEnabled(updated.getEmailEnabled());
        existing.setWebhookEnabled(updated.getWebhookEnabled());
        existing.setRateLimitWarningEnabled(updated.getRateLimitWarningEnabled());
        existing.setRateLimitExceededEnabled(updated.getRateLimitExceededEnabled());
        existing.setApiKeyExpiringEnabled(updated.getApiKeyExpiringEnabled());
        existing.setDailyReportEnabled(updated.getDailyReportEnabled());
        existing.setHighErrorRateEnabled(updated.getHighErrorRateEnabled());

        return preferenceRepository.save(existing);
    }
}