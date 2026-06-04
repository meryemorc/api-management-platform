package com.example.billingservice.controller;

import com.example.billingservice.dto.request.ChangePlanRequest;
import com.example.billingservice.dto.request.CreateSubscriptionRequest;
import com.example.billingservice.dto.response.BillingPlanResponse;
import com.example.billingservice.dto.response.SubscriptionResponse;
import com.example.billingservice.entity.Subscription;
import com.example.billingservice.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/{organizationId}")
    public ResponseEntity<SubscriptionResponse> createSubscription(
            @PathVariable UUID organizationId,
            @Valid @RequestBody CreateSubscriptionRequest request) {
        Subscription subscription = subscriptionService.createSubscription(
                organizationId, request.getPlanName(), request.getBillingCycle());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(subscription));
    }

    @GetMapping("/{organizationId}")
    public ResponseEntity<SubscriptionResponse> getSubscription(
            @PathVariable UUID organizationId) {
        Subscription subscription = subscriptionService.getByOrganizationId(organizationId);
        return ResponseEntity.ok(toResponse(subscription));
    }

    @PutMapping("/{organizationId}/plan")
    public ResponseEntity<SubscriptionResponse> changePlan(
            @PathVariable UUID organizationId,
            @Valid @RequestBody ChangePlanRequest request) {
        Subscription subscription = subscriptionService.changePlan(
                organizationId, request.getPlanName());
        return ResponseEntity.ok(toResponse(subscription));
    }

    @DeleteMapping("/{organizationId}")
    public ResponseEntity<SubscriptionResponse> cancelSubscription(
            @PathVariable UUID organizationId) {
        Subscription subscription = subscriptionService.cancelSubscription(organizationId);
        return ResponseEntity.ok(toResponse(subscription));
    }

    private SubscriptionResponse toResponse(Subscription subscription) {
        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .organizationId(subscription.getOrganizationId())
                .plan(BillingPlanResponse.builder()
                        .id(subscription.getPlan().getId())
                        .name(subscription.getPlan().getName())
                        .displayName(subscription.getPlan().getDisplayName())
                        .monthlyPrice(subscription.getPlan().getMonthlyPrice())
                        .yearlyPrice(subscription.getPlan().getYearlyPrice())
                        .requestLimit(subscription.getPlan().getRequestLimit())
                        .overagePricePerRequest(subscription.getPlan().getOveragePricePerRequest())
                        .features(subscription.getPlan().getFeatures())
                        .isActive(subscription.getPlan().getIsActive())
                        .build())
                .status(subscription.getStatus())
                .billingCycle(subscription.getBillingCycle())
                .currentPeriodStart(subscription.getCurrentPeriodStart())
                .currentPeriodEnd(subscription.getCurrentPeriodEnd())
                .cancelAtPeriodEnd(subscription.getCancelAtPeriodEnd())
                .createdAt(subscription.getCreatedAt())
                .build();
    }
}