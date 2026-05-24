package com.example.notificationservice.controller;

import com.example.notificationservice.dto.response.NotificationLogResponse;
import com.example.notificationservice.entity.NotificationLog;
import com.example.notificationservice.event.NotificationEvent;
import com.example.notificationservice.repository.NotificationLogRepository;
import com.example.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationLogController {

    private final NotificationLogRepository notificationLogRepository;
    private final NotificationService notificationService;

    @GetMapping("/{organizationId}/logs")
    public ResponseEntity<List<NotificationLogResponse>> getLogs(
            @PathVariable UUID organizationId) {

        List<NotificationLogResponse> logs = notificationLogRepository
                .findByOrganizationIdOrderByCreatedAtDesc(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(logs);
    }

    @PostMapping("/test")
    public ResponseEntity<String> test(@RequestBody NotificationEvent event) {
        notificationService.processNotification(event);
        return ResponseEntity.ok("Event processed");
    }

    private NotificationLogResponse toResponse(NotificationLog log) {
        return NotificationLogResponse.builder()
                .id(log.getId())
                .organizationId(log.getOrganizationId())
                .recipientEmail(log.getRecipientEmail())
                .notificationType(log.getNotificationType())
                .channel(log.getChannel())
                .status(log.getStatus())
                .subject(log.getSubject())
                .errorMessage(log.getErrorMessage())
                .retryCount(log.getRetryCount())
                .sentAt(log.getSentAt())
                .createdAt(log.getCreatedAt())
                .build();
    }
}