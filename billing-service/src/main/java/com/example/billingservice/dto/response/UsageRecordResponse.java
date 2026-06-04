package com.example.billingservice.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UsageRecordResponse {
    private UUID id;
    private UUID organizationId;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private Long totalRequests;
    private Integer includedRequests;
    private Long overageRequests;
    private BigDecimal overageAmount;
    private Double usagePercent;
}