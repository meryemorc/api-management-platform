package com.example.notificationservice.service;

import com.example.notificationservice.entity.NotificationLog;
import com.example.notificationservice.enums.NotificationStatus;
import com.example.notificationservice.enums.NotificationType;
import com.example.notificationservice.event.NotificationEvent;
import com.example.notificationservice.repository.NotificationLogRepository;
import com.example.notificationservice.repository.NotificationPreferenceRepository;
import com.example.notificationservice.service.channel.EmailNotificationChannel;
import com.example.notificationservice.service.channel.WebhookNotificationChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @Mock
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @Mock
    private EmailNotificationChannel emailChannel;

    @Mock
    private WebhookNotificationChannel webhookChannel;

    @Mock
    private EmailTemplateService emailTemplateService;

    @InjectMocks
    private NotificationService notificationService;

    private NotificationEvent testEvent;
    private UUID organizationId;

    @BeforeEach
    void setUp() {
        organizationId = UUID.randomUUID();
        testEvent = NotificationEvent.builder()
                .organizationId(organizationId)
                .recipientEmail("test@test.com")
                .recipientName("Test User")
                .notificationType(NotificationType.RATE_LIMIT_WARNING)
                .metadata(Map.of(
                        "recipientName", "Test User",
                        "usagePercent", "85",
                        "usedRequests", "850",
                        "dailyLimit", "1000"
                ))
                .build();
    }

    @Test
    @DisplayName("Duplicate notification should be skipped")
    void shouldSkipDuplicateNotification() {
        when(notificationLogRepository
                .existsByOrganizationIdAndNotificationTypeAndCreatedAtAfter(
                        eq(organizationId),
                        eq(NotificationType.RATE_LIMIT_WARNING),
                        any(LocalDateTime.class)))
                .thenReturn(true);

        notificationService.processNotification(testEvent);

        verify(emailChannel, never()).send(any(), any(), any());
        verify(notificationLogRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should send email when preference is default")
    void shouldSendEmailWithDefaultPreference() {
        when(notificationLogRepository
                .existsByOrganizationIdAndNotificationTypeAndCreatedAtAfter(
                        any(), any(), any()))
                .thenReturn(false);

        when(notificationPreferenceRepository.findByOrganizationId(organizationId))
                .thenReturn(Optional.empty());

        when(emailTemplateService.resolveSubject(testEvent))
                .thenReturn("⚠️ Rate Limit Warning");

        when(emailTemplateService.resolveTemplateName(testEvent))
                .thenReturn("rate_limit_warning");

        when(emailTemplateService.render(any(), any()))
                .thenReturn("<html>test</html>");

        when(notificationLogRepository.save(any(NotificationLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        notificationService.processNotification(testEvent);

        verify(emailChannel, times(1)).send(eq(testEvent), any(), any());
    }

    @Test
    @DisplayName("Should not send email when email is disabled")
    void shouldNotSendEmailWhenDisabled() {
        when(notificationLogRepository
                .existsByOrganizationIdAndNotificationTypeAndCreatedAtAfter(
                        any(), any(), any()))
                .thenReturn(false);

        var preference = new com.example.notificationservice.entity.NotificationPreference();
        preference.setEmailEnabled(false);
        preference.setWebhookEnabled(false);

        when(notificationPreferenceRepository.findByOrganizationId(organizationId))
                .thenReturn(Optional.of(preference));

        notificationService.processNotification(testEvent);

        verify(emailChannel, never()).send(any(), any(), any());
    }

    @Test
    @DisplayName("Should save FAILED log when email sending fails")
    void shouldSaveFailedLogWhenEmailFails() {
        when(notificationLogRepository
                .existsByOrganizationIdAndNotificationTypeAndCreatedAtAfter(
                        any(), any(), any()))
                .thenReturn(false);

        when(notificationPreferenceRepository.findByOrganizationId(organizationId))
                .thenReturn(Optional.empty());

        when(emailTemplateService.resolveSubject(any())).thenReturn("subject");
        when(emailTemplateService.resolveTemplateName(any())).thenReturn("template");
        when(emailTemplateService.render(any(), any())).thenReturn("<html/>");

        doThrow(new RuntimeException("Resend API error"))
                .when(emailChannel).send(any(), any(), any());

        when(notificationLogRepository.save(any(NotificationLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        notificationService.processNotification(testEvent);

        verify(notificationLogRepository).save(argThat(log ->
                log.getStatus() == NotificationStatus.FAILED
                        && log.getErrorMessage() != null
        ));
    }
}