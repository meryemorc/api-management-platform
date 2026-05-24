package com.example.notificationservice.service.channel;

import com.example.notificationservice.event.NotificationEvent;
import com.example.notificationservice.exception.NotificationException;
import com.example.notificationservice.repository.WebhookSubscriptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookNotificationChannel implements NotificationChannel {

    private final WebhookSubscriptionRepository webhookSubscriptionRepository;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    @Retry(name = "webhookSender", fallbackMethod = "sendFallback")
    public void send(NotificationEvent event, String subject, String htmlContent) {
        var subscriptions = webhookSubscriptionRepository
                .findByOrganizationIdAndIsActiveTrue(event.getOrganizationId());

        for (var subscription : subscriptions) {
            if (!subscription.getEventTypes().contains(event.getNotificationType().name())) {
                continue;
            }
            try {
                Map<String, Object> payload = Map.of(
                        "organizationId", event.getOrganizationId().toString(),
                        "notificationType", event.getNotificationType().name(),
                        "recipientEmail", event.getRecipientEmail(),
                        "metadata", event.getMetadata() != null ? event.getMetadata() : Map.of()
                );

                String body = objectMapper.writeValueAsString(payload);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(subscription.getUrl()))
                        .header("Content-Type", "application/json")
                        .header("X-Webhook-Event", event.getNotificationType().name())
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();

                HttpResponse<String> response = httpClient.send(request,
                        HttpResponse.BodyHandlers.ofString());

                log.info("Webhook sent to {} — status: {}", subscription.getUrl(),
                        response.statusCode());

            } catch (Exception e) {
                log.error("Webhook failed for url {}: {}", subscription.getUrl(), e.getMessage());
                throw new NotificationException("Webhook failed: " + e.getMessage(), e);
            }
        }
    }

    public void sendFallback(NotificationEvent event, String subject,
                             String htmlContent, Exception ex) {
        log.error("All webhook retry attempts failed for org {}. Error: {}",
                event.getOrganizationId(), ex.getMessage());
    }

    @Override
    public boolean supports(com.example.notificationservice.enums.NotificationChannel channel) {
        return channel == com.example.notificationservice.enums.NotificationChannel.WEBHOOK;
    }
}