package com.example.billingservice.service;

import com.example.billingservice.entity.Subscription;
import com.example.billingservice.entity.UsageRecord;
import com.example.billingservice.enums.BillingCycle;
import com.example.billingservice.enums.SubscriptionStatus;
import com.example.billingservice.repository.SubscriptionRepository;
import com.example.billingservice.repository.UsageRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsageTrackingService {

    private final UsageRecordRepository usageRecordRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public void trackRequest(UUID organizationId) {
        Subscription subscription = subscriptionRepository
                .findByOrganizationId(organizationId)
                .orElse(null);

        if (subscription == null || subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            return;
        }

        LocalDateTime periodStart = getCurrentPeriodStart(subscription);
        LocalDateTime periodEnd = subscription.getCurrentPeriodEnd();

        UsageRecord record = usageRecordRepository
                .findByOrganizationIdAndPeriodStartAndPeriodEnd(organizationId, periodStart, periodEnd)
                .orElseGet(() -> createNewUsageRecord(subscription, periodStart, periodEnd));

        record.setTotalRequests(record.getTotalRequests() + 1);

        // Overage hesapla
        int limit = subscription.getPlan().getRequestLimit();
        if (record.getTotalRequests() > limit) {
            long overageRequests = record.getTotalRequests() - limit;
            record.setOverageRequests(overageRequests);
            BigDecimal overageAmount = subscription.getPlan().getOveragePricePerRequest()
                    .multiply(BigDecimal.valueOf(overageRequests));
            record.setOverageAmount(overageAmount);
        }

        usageRecordRepository.save(record);
    }

    private UsageRecord createNewUsageRecord(Subscription subscription,
                                             LocalDateTime periodStart,
                                             LocalDateTime periodEnd) {
        return UsageRecord.builder()
                .organizationId(subscription.getOrganizationId())
                .subscription(subscription)
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .totalRequests(0L)
                .includedRequests(subscription.getPlan().getRequestLimit())
                .overageRequests(0L)
                .overageAmount(BigDecimal.ZERO)
                .build();
    }

    private LocalDateTime getCurrentPeriodStart(Subscription subscription) {
        LocalDateTime now = LocalDateTime.now();
        if (subscription.getBillingCycle() == BillingCycle.MONTHLY) {
            return now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
        return now.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    public UsageRecord getCurrentUsage(UUID organizationId) {
        Subscription subscription = subscriptionRepository
                .findByOrganizationId(organizationId)
                .orElseThrow(() -> new com.example.billingservice.exception.ResourceNotFoundException(
                        "Subscription not found: " + organizationId));

        LocalDateTime periodStart = getCurrentPeriodStart(subscription);
        LocalDateTime periodEnd = subscription.getCurrentPeriodEnd();

        return usageRecordRepository
                .findByOrganizationIdAndPeriodStartAndPeriodEnd(organizationId, periodStart, periodEnd)
                .orElseGet(() -> UsageRecord.builder()
                        .organizationId(organizationId)
                        .totalRequests(0L)
                        .overageRequests(0L)
                        .overageAmount(BigDecimal.ZERO)
                        .build());
    }
}