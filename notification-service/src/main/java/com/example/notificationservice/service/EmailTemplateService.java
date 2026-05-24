package com.example.notificationservice.service;

import com.example.notificationservice.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailTemplateService {

    private final TemplateEngine templateEngine;

    public String render(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        if (variables != null) {
            variables.forEach(context::setVariable);
        }
        return templateEngine.process("email/" + templateName, context);
    }

    public String resolveTemplateName(NotificationEvent event) {
        return switch (event.getNotificationType()) {
            case RATE_LIMIT_WARNING -> "rate_limit_warning";
            case RATE_LIMIT_EXCEEDED -> "rate_limit_exceeded";
            case API_KEY_EXPIRING -> "api_key_expiring";
            case API_KEY_EXPIRED -> "api_key_expiring";
            case HIGH_ERROR_RATE -> "high_error_rate";
            case DAILY_REPORT -> "rate_limit_warning";
        };
    }

    public String resolveSubject(NotificationEvent event) {
        return switch (event.getNotificationType()) {
            case RATE_LIMIT_WARNING -> "⚠️ API Rate Limit Warning - 80% Used";
            case RATE_LIMIT_EXCEEDED -> "🚫 API Rate Limit Exceeded";
            case API_KEY_EXPIRING -> "🔑 Your API Key is Expiring Soon";
            case API_KEY_EXPIRED -> "🔑 Your API Key has Expired";
            case HIGH_ERROR_RATE -> "🔴 High Error Rate Detected";
            case DAILY_REPORT -> "📊 Your Daily API Usage Report";
        };
    }
}