package com.example.notificationservice.service;

import com.example.notificationservice.enums.NotificationType;
import com.example.notificationservice.event.NotificationEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailTemplateServiceTest {

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailTemplateService emailTemplateService;

    @Test
    @DisplayName("Should resolve correct template name for RATE_LIMIT_WARNING")
    void shouldResolveRateLimitWarningTemplate() {
        NotificationEvent event = buildEvent(NotificationType.RATE_LIMIT_WARNING);
        assertThat(emailTemplateService.resolveTemplateName(event))
                .isEqualTo("rate_limit_warning");
    }

    @Test
    @DisplayName("Should resolve correct template name for RATE_LIMIT_EXCEEDED")
    void shouldResolveRateLimitExceededTemplate() {
        NotificationEvent event = buildEvent(NotificationType.RATE_LIMIT_EXCEEDED);
        assertThat(emailTemplateService.resolveTemplateName(event))
                .isEqualTo("rate_limit_exceeded");
    }

    @Test
    @DisplayName("Should resolve correct template name for HIGH_ERROR_RATE")
    void shouldResolveHighErrorRateTemplate() {
        NotificationEvent event = buildEvent(NotificationType.HIGH_ERROR_RATE);
        assertThat(emailTemplateService.resolveTemplateName(event))
                .isEqualTo("high_error_rate");
    }

    @Test
    @DisplayName("Should resolve correct subject for RATE_LIMIT_WARNING")
    void shouldResolveCorrectSubject() {
        NotificationEvent event = buildEvent(NotificationType.RATE_LIMIT_WARNING);
        assertThat(emailTemplateService.resolveSubject(event))
                .isEqualTo("⚠️ API Rate Limit Warning - 80% Used");
    }

    @Test
    @DisplayName("Should resolve correct subject for RATE_LIMIT_EXCEEDED")
    void shouldResolveExceededSubject() {
        NotificationEvent event = buildEvent(NotificationType.RATE_LIMIT_EXCEEDED);
        assertThat(emailTemplateService.resolveSubject(event))
                .isEqualTo("🚫 API Rate Limit Exceeded");
    }

    @Test
    @DisplayName("Should render template with variables")
    void shouldRenderTemplateWithVariables() {
        Map<String, Object> variables = Map.of(
                "recipientName", "Test User",
                "usagePercent", "85"
        );
        when(templateEngine.process(eq("email/rate_limit_warning"), any(Context.class)))
                .thenReturn("<html>Test User - 85%</html>");

        String result = emailTemplateService.render("rate_limit_warning", variables);

        assertThat(result).contains("Test User");
        assertThat(result).contains("85%");
        verify(templateEngine).process(eq("email/rate_limit_warning"), any(Context.class));
    }

    private NotificationEvent buildEvent(NotificationType type) {
        return NotificationEvent.builder()
                .organizationId(UUID.randomUUID())
                .recipientEmail("test@test.com")
                .recipientName("Test User")
                .notificationType(type)
                .metadata(Map.of())
                .build();
    }
}