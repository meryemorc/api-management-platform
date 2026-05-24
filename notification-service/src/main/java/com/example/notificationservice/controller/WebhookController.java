package com.example.notificationservice.controller;

import com.example.notificationservice.dto.request.WebhookSubscriptionRequest;
import com.example.notificationservice.dto.response.WebhookSubscriptionResponse;
import com.example.notificationservice.entity.WebhookSubscription;
import com.example.notificationservice.exception.ResourceNotFoundException;
import com.example.notificationservice.repository.WebhookSubscriptionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookSubscriptionRepository webhookSubscriptionRepository;

    @PostMapping("/{organizationId}/webhooks")
    public ResponseEntity<WebhookSubscriptionResponse> createWebhook(
            @PathVariable UUID organizationId,
            @Valid @RequestBody WebhookSubscriptionRequest request) {

        WebhookSubscription subscription = WebhookSubscription.builder()
                .organizationId(organizationId)
                .url(request.getUrl())
                .secret(request.getSecret())
                .eventTypes(request.getEventTypes())
                .isActive(true)
                .build();

        WebhookSubscription saved = webhookSubscriptionRepository.save(subscription);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping("/{organizationId}/webhooks")
    public ResponseEntity<List<WebhookSubscriptionResponse>> getWebhooks(
            @PathVariable UUID organizationId) {

        List<WebhookSubscriptionResponse> webhooks = webhookSubscriptionRepository
                .findByOrganizationIdAndIsActiveTrue(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(webhooks);
    }

    @DeleteMapping("/{organizationId}/webhooks/{webhookId}")
    public ResponseEntity<Void> deleteWebhook(
            @PathVariable UUID organizationId,
            @PathVariable UUID webhookId) {

        WebhookSubscription subscription = webhookSubscriptionRepository.findById(webhookId)
                .orElseThrow(() -> new ResourceNotFoundException("Webhook not found: " + webhookId));

        subscription.setIsActive(false);
        webhookSubscriptionRepository.save(subscription);
        return ResponseEntity.noContent().build();
    }

    private WebhookSubscriptionResponse toResponse(WebhookSubscription subscription) {
        return WebhookSubscriptionResponse.builder()
                .id(subscription.getId())
                .organizationId(subscription.getOrganizationId())
                .url(subscription.getUrl())
                .isActive(subscription.getIsActive())
                .eventTypes(subscription.getEventTypes())
                .createdAt(subscription.getCreatedAt())
                .build();
    }
}