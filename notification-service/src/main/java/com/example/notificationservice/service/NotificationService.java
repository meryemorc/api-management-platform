package com.example.notificationservice.service;

import com.example.notificationservice.entity.NotificationLog;
import com.example.notificationservice.enums.NotificationChannel;
import com.example.notificationservice.enums.NotificationStatus;
import com.example.notificationservice.enums.NotificationType;
import com.example.notificationservice.event.NotificationEvent;
import com.example.notificationservice.repository.NotificationLogRepository;
import com.example.notificationservice.repository.NotificationPreferenceRepository;
import com.example.notificationservice.service.channel.EmailNotificationChannel;
import com.example.notificationservice.service.channel.WebhookNotificationChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationLogRepository notificationLogRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final EmailNotificationChannel emailChannel;
    private final WebhookNotificationChannel webhookChannel;
    private final EmailTemplateService emailTemplateService;

    public void processNotification(NotificationEvent event) {
        log.info("Processing notification: type={}, org={}",
                event.getNotificationType(), event.getOrganizationId());

        // Aynı tipte son 1 saatte bildirim gittiyse tekrar gönderme
        if (isDuplicate(event)) {
            log.info("Duplicate notification skipped: type={}, org={}",
                    event.getNotificationType(), event.getOrganizationId());
            return;
        }

        var preference = notificationPreferenceRepository
                .findByOrganizationId(event.getOrganizationId())
                .orElse(null);

        // Preference yoksa default olarak email gönder
        boolean emailEnabled = preference == null || Boolean.TRUE.equals(preference.getEmailEnabled());
        boolean webhookEnabled = preference != null && Boolean.TRUE.equals(preference.getWebhookEnabled());

        if (emailEnabled && event.getRecipientEmail() != null) {
            sendViaChannel(event, NotificationChannel.EMAIL);
        }

        if (webhookEnabled) {
            sendViaChannel(event, NotificationChannel.WEBHOOK);
        }
    }

    private void sendViaChannel(NotificationEvent event, NotificationChannel channel) {
        String subject = emailTemplateService.resolveSubject(event);
        String templateName = emailTemplateService.resolveTemplateName(event);
        String htmlContent = emailTemplateService.render(templateName, event.getMetadata());

        NotificationLog log = NotificationLog.builder()
                .organizationId(event.getOrganizationId())
                .recipientEmail(event.getRecipientEmail())
                .notificationType(event.getNotificationType())
                .channel(channel)
                .status(NotificationStatus.PENDING)
                .subject(subject)
                .retryCount(0)
                .build();

        try {
            if (channel == NotificationChannel.EMAIL) {
                emailChannel.send(event, subject, htmlContent);
            } else {
                webhookChannel.send(event, subject, htmlContent);
            }
            log.setStatus(NotificationStatus.SENT);
            log.setSentAt(LocalDateTime.now());

        } catch (Exception e) {
            log.setStatus(NotificationStatus.FAILED);
            log.setErrorMessage(e.getMessage());
        } finally {
            notificationLogRepository.save(log);
        }
    }

    private boolean isDuplicate(NotificationEvent event) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        return notificationLogRepository.existsByOrganizationIdAndNotificationTypeAndCreatedAtAfter(
                event.getOrganizationId(),
                event.getNotificationType(),
                oneHourAgo
        );
    }
}