package com.example.notificationservice.service;

import com.example.notificationservice.entity.NotificationPreference;
import com.example.notificationservice.enums.NotificationType;
import com.example.notificationservice.event.NotificationEvent;
import com.example.notificationservice.repository.NotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSchedulerService {

    private final NotificationService notificationService;
    private final NotificationPreferenceRepository preferenceRepository;

    // Her gece 00:01'de çalışır - günlük sayaçları sıfırla
    @Scheduled(cron = "0 1 0 * * *")
    public void resetDailyCounters() {
        log.info("Daily counters reset triggered");
        // ApiRequestConsumerService'teki in-memory counter'lar uygulama restart'ında zaten sıfırlanır
        // Production'da Redis kullanılmalı
    }

    // Her sabah 08:00'de çalışır - günlük rapor
    @Scheduled(cron = "0 0 8 * * *")
    public void sendDailyReports() {
        log.info("Daily report scheduler triggered");

        List<NotificationPreference> preferences = preferenceRepository.findAll();

        preferences.stream()
                .filter(p -> Boolean.TRUE.equals(p.getDailyReportEnabled()))
                .forEach(pref -> {
                    NotificationEvent event = NotificationEvent.builder()
                            .organizationId(pref.getOrganizationId())
                            .recipientEmail("admin@organization.com")
                            .recipientName("Organization Admin")
                            .notificationType(NotificationType.DAILY_REPORT)
                            .metadata(Map.of(
                                    "recipientName", "Organization Admin",
                                    "usagePercent", "0",
                                    "usedRequests", "0",
                                    "dailyLimit", "1000"
                            ))
                            .build();

                    notificationService.processNotification(event);
                    log.info("Daily report sent for org: {}", pref.getOrganizationId());
                });
    }

    // Her gece 23:00'de çalışır - expire olacak key'leri kontrol et
    @Scheduled(cron = "0 0 23 * * *")
    public void checkExpiringApiKeys() {
        log.info("API key expiry check scheduler triggered");
        // Organization service'ten API key'leri çekmek gerekiyor
        // Şimdilik log yazıyoruz, organization service entegrasyonu eklenince tamamlanacak
        log.info("API key expiry check completed - organization service integration pending");
    }
}