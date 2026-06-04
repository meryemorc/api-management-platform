package com.example.billingservice.controller;

import com.example.billingservice.dto.response.UsageRecordResponse;
import com.example.billingservice.entity.UsageRecord;
import com.example.billingservice.repository.UsageRecordRepository;
import com.example.billingservice.service.UsageTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing/usage")
@RequiredArgsConstructor
public class UsageController {

    private final UsageTrackingService usageTrackingService;
    private final UsageRecordRepository usageRecordRepository;

    @GetMapping("/{organizationId}/current")
    public ResponseEntity<UsageRecordResponse> getCurrentUsage(
            @PathVariable UUID organizationId) {
        UsageRecord record = usageTrackingService.getCurrentUsage(organizationId);
        return ResponseEntity.ok(toResponse(record));
    }

    @GetMapping("/{organizationId}/history")
    public ResponseEntity<List<UsageRecordResponse>> getUsageHistory(
            @PathVariable UUID organizationId) {
        List<UsageRecordResponse> history = usageRecordRepository
                .findByOrganizationIdOrderByPeriodStartDesc(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(history);
    }

    private UsageRecordResponse toResponse(UsageRecord record) {
        double usagePercent = 0.0;
        if (record.getIncludedRequests() != null && record.getIncludedRequests() > 0) {
            usagePercent = (double) record.getTotalRequests() / record.getIncludedRequests() * 100;
        }

        return UsageRecordResponse.builder()
                .id(record.getId())
                .organizationId(record.getOrganizationId())
                .periodStart(record.getPeriodStart())
                .periodEnd(record.getPeriodEnd())
                .totalRequests(record.getTotalRequests())
                .includedRequests(record.getIncludedRequests())
                .overageRequests(record.getOverageRequests())
                .overageAmount(record.getOverageAmount())
                .usagePercent(Math.min(usagePercent, 100.0))
                .build();
    }
}