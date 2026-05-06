package com.example.analyticsservice.controller;

import com.example.analyticsservice.document.ApiRequestDocument;
import com.example.analyticsservice.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "API kullanım istatistikleri")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Operation(summary = "Günlük istek sayısı")
    @GetMapping("/{organizationId}/daily")
    public ResponseEntity<Map<String, Object>> getDailyStats(
            @PathVariable String organizationId) {
        return ResponseEntity.ok(Map.of(
                "organizationId", organizationId,
                "date", java.time.LocalDate.now().toString(),
                "totalRequests", analyticsService.getDailyRequestCount(organizationId)
        ));
    }

    @Operation(summary = "Aylık istek sayısı")
    @GetMapping("/{organizationId}/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyStats(
            @PathVariable String organizationId) {
        return ResponseEntity.ok(Map.of(
                "organizationId", organizationId,
                "month", java.time.YearMonth.now().toString(),
                "totalRequests", analyticsService.getMonthlyRequestCount(organizationId)
        ));
    }

    @Operation(summary = "Son 24 saatin istekleri")
    @GetMapping("/{organizationId}/recent")
    public ResponseEntity<List<ApiRequestDocument>> getRecentRequests(
            @PathVariable String organizationId) {
        return ResponseEntity.ok(analyticsService.getRecentRequests(organizationId));
    }

    @Operation(summary = "En çok kullanılan endpointler")
    @GetMapping("/{organizationId}/endpoints")
    public ResponseEntity<Map<String, Long>> getTopEndpoints(
            @PathVariable String organizationId) {
        return ResponseEntity.ok(analyticsService.getTopEndpoints(organizationId));
    }

    @Operation(summary = "Ortalama yanıt süresi")
    @GetMapping("/{organizationId}/response-time")
    public ResponseEntity<Map<String, Object>> getAverageResponseTime(
            @PathVariable String organizationId) {
        return ResponseEntity.ok(Map.of(
                "organizationId", organizationId,
                "averageResponseTimeMs", analyticsService.getAverageResponseTime(organizationId)
        ));
    }
}