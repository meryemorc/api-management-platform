package com.example.billingservice.dto.response;

import com.example.billingservice.enums.BillingCycle;
import com.example.billingservice.enums.SubscriptionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class SubscriptionResponse {
    private UUID id;
    private UUID organizationId;
    private BillingPlanResponse plan;
    private SubscriptionStatus status;
    private BillingCycle billingCycle;
    private LocalDateTime currentPeriodStart;
    private LocalDateTime currentPeriodEnd;
    private Boolean cancelAtPeriodEnd;
    private LocalDateTime createdAt;
}