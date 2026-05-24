package com.example.notificationservice.service.channel;

import com.example.notificationservice.event.NotificationEvent;
import com.example.notificationservice.exception.NotificationException;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationChannel implements NotificationChannel {

    private final Resend resendClient;

    @Value("${resend.from-email}")
    private String fromEmail;

    @Value("${resend.from-name}")
    private String fromName;

    @Override
    @Retry(name = "emailSender", fallbackMethod = "sendFallback")
    public void send(NotificationEvent event, String subject, String htmlContent) {
        try {
            CreateEmailOptions options = CreateEmailOptions.builder()
                    .from(fromName + " <" + fromEmail + ">")
                    .to(event.getRecipientEmail())
                    .subject(subject)
                    .html(htmlContent)
                    .build();

            resendClient.emails().send(options);
            log.info("Email sent to {} for event type {}",
                    event.getRecipientEmail(), event.getNotificationType());

        } catch (ResendException e) {
            log.error("Failed to send email to {}: {}", event.getRecipientEmail(), e.getMessage());
            throw new NotificationException("Email sending failed: " + e.getMessage(), e);
        }
    }

    public void sendFallback(NotificationEvent event, String subject,
                             String htmlContent, Exception ex) {
        log.error("All retry attempts failed for email to {}. Giving up. Error: {}",
                event.getRecipientEmail(), ex.getMessage());
    }

    @Override
    public boolean supports(com.example.notificationservice.enums.NotificationChannel channel) {
        return channel == com.example.notificationservice.enums.NotificationChannel.EMAIL;
    }
}