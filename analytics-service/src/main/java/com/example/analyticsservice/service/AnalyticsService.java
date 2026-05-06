package com.example.analyticsservice.service;

import com.example.analyticsservice.document.ApiRequestDocument;
import com.example.analyticsservice.event.ApiRequestEvent;
import com.example.analyticsservice.repository.ApiRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final ApiRequestRepository apiRequestRepository;

    public void saveRequest(ApiRequestEvent event) {
        ApiRequestDocument document = ApiRequestDocument.builder()
                .organizationId(event.getOrganizationId().toString())
                .apiKeyId(event.getApiKeyId())
                .path(event.getPath())
                .method(event.getMethod())
                .statusCode(event.getStatusCode())
                .responseTimeMs(event.getResponseTimeMs())
                .timestamp(event.getTimestamp())
                .build();

        apiRequestRepository.save(document);
        log.info("Event kaydedildi: org={}, path={}", event.getOrganizationId(), event.getPath());
    }

    public long getDailyRequestCount(String organizationId) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return apiRequestRepository.countByOrganizationIdAndTimestampBetween(
                organizationId, startOfDay, endOfDay);
    }

    public long getMonthlyRequestCount(String organizationId) {
        LocalDateTime startOfMonth = LocalDateTime.now()
                .withDayOfMonth(1).toLocalDate().atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        return apiRequestRepository.countByOrganizationIdAndTimestampBetween(
                organizationId, startOfMonth, endOfMonth);
    }

    public List<ApiRequestDocument> getRecentRequests(String organizationId) {
        LocalDateTime last24h = LocalDateTime.now().minusHours(24);
        return apiRequestRepository.findByOrganizationIdAndTimestampBetween(
                organizationId, last24h, LocalDateTime.now());
    }

    public Map<String, Long> getTopEndpoints(String organizationId) {
        // Son 30 günün en çok kullanılan endpoint'leri
        LocalDateTime last30Days = LocalDateTime.now().minusDays(30);
        List<ApiRequestDocument> requests = apiRequestRepository
                .findByOrganizationIdAndTimestampBetween(
                        organizationId, last30Days, LocalDateTime.now());

        return requests.stream()
                .collect(Collectors.groupingBy(
                        ApiRequestDocument::getPath,
                        Collectors.counting()
                ));
    }

    public double getAverageResponseTime(String organizationId) {
        LocalDateTime last24h = LocalDateTime.now().minusHours(24);
        List<ApiRequestDocument> requests = apiRequestRepository
                .findByOrganizationIdAndTimestampBetween(
                        organizationId, last24h, LocalDateTime.now());

        return requests.stream()
                .mapToLong(ApiRequestDocument::getResponseTimeMs)
                .average()
                .orElse(0.0);
    }
}