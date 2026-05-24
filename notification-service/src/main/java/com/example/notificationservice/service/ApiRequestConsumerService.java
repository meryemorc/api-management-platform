package com.example.notificationservice.service;

import com.example.notificationservice.event.ApiRequestEvent;
import com.example.notificationservice.event.NotificationEvent;
import com.example.notificationservice.enums.NotificationType;
import com.example.notificationservice.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiRequestConsumerService {

    private final NotificationService notificationService;
    private final NotificationLogRepository notificationLogRepository;

    // Her organizasyon için o günkü istek sayısını tut
    private final ConcurrentHashMap<UUID, AtomicInteger> dailyRequestCounts = new ConcurrentHashMap<>();
    // Her organizasyon için son 5 dakikadaki hata sayısını tut
    private final ConcurrentHashMap<UUID, AtomicInteger> recentErrorCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, AtomicInteger> recentTotalCounts = new ConcurrentHashMap<>();

    @KafkaListener(
            topics = "api-requests",
            groupId = "notification-group",
            containerFactory = "apiRequestKafkaListenerContainerFactory"
    )
    public void consume(ApiRequestEvent event) {
        if (event == null || event.getOrganizationId() == null) return;

        UUID orgId = event.getOrganizationId();
        log.debug("API request event received: org={}, status={}", orgId, event.getStatusCode());

        trackDailyRequests(event);
        trackErrorRate(event);
    }

    private void trackDailyRequests(ApiRequestEvent event) {
        UUID orgId = event.getOrganizationId();

        dailyRequestCounts.computeIfAbsent(orgId, k -> new AtomicInteger(0));
        int count = dailyRequestCounts.get(orgId).incrementAndGet();

        // Basit limit kontrolü — gerçekte organization service'ten limit çekilmeli
        int dailyLimit = 1000;
        double usagePercent = (double) count / dailyLimit * 100;

        if (usagePercent >= 100) {
            sendNotification(orgId, event, NotificationType.RATE_LIMIT_EXCEEDED,
                    Map.of(
                            "recipientName", "Organization Admin",
                            "dailyLimit", String.valueOf(dailyLimit),
                            "usedRequests", String.valueOf(count)
                    ));
        } else if (usagePercent >= 80) {
            sendNotification(orgId, event, NotificationType.RATE_LIMIT_WARNING,
                    Map.of(
                            "recipientName", "Organization Admin",
                            "usagePercent", String.format("%.0f", usagePercent),
                            "usedRequests", String.valueOf(count),
                            "dailyLimit", String.valueOf(dailyLimit)
                    ));
        }
    }

    private void trackErrorRate(ApiRequestEvent event) {
        UUID orgId = event.getOrganizationId();

        recentTotalCounts.computeIfAbsent(orgId, k -> new AtomicInteger(0));
        recentErrorCounts.computeIfAbsent(orgId, k -> new AtomicInteger(0));

        recentTotalCounts.get(orgId).incrementAndGet();
        if (event.getStatusCode() >= 500) {
            recentErrorCounts.get(orgId).incrementAndGet();
        }

        int total = recentTotalCounts.get(orgId).get();
        int errors = recentErrorCounts.get(orgId).get();

        if (total >= 10) {
            double errorRate = (double) errors / total * 100;
            if (errorRate >= 10) {
                sendNotification(orgId, event, NotificationType.HIGH_ERROR_RATE,
                        Map.of(
                                "recipientName", "Organization Admin",
                                "errorRate", String.format("%.0f", errorRate),
                                "totalRequests", String.valueOf(total),
                                "failedRequests", String.valueOf(errors)
                        ));
                // Reset counters after alert
                recentTotalCounts.get(orgId).set(0);
                recentErrorCounts.get(orgId).set(0);
            }
        }
    }

    private void sendNotification(UUID orgId, ApiRequestEvent event,
                                  NotificationType type, Map<String, Object> metadata) {
        // Son 1 saatte aynı tipte bildirim gittiyse gönderme
        boolean alreadySent = notificationLogRepository
                .existsByOrganizationIdAndNotificationTypeAndCreatedAtAfter(
                        orgId, type, LocalDateTime.now().minusHours(1));

        if (alreadySent) return;

        Map<String, Object> mutableMetadata = new HashMap<>(metadata);

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .organizationId(orgId)
                .recipientEmail("admin@organization.com") // gerçekte DB'den çekilmeli
                .recipientName((String) mutableMetadata.getOrDefault("recipientName", "Admin"))
                .notificationType(type)
                .metadata(mutableMetadata)
                .build();

        notificationService.processNotification(notificationEvent);
        log.info("Notification triggered: type={}, org={}", type, orgId);
    }
}