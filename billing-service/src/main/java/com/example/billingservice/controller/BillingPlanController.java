package com.example.billingservice.controller;

import com.example.billingservice.dto.response.BillingPlanResponse;
import com.example.billingservice.entity.BillingPlan;
import com.example.billingservice.repository.BillingPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/billing/plans")
@RequiredArgsConstructor
public class BillingPlanController {

    private final BillingPlanRepository billingPlanRepository;

    @GetMapping
    public ResponseEntity<List<BillingPlanResponse>> getAllPlans() {
        List<BillingPlanResponse> plans = billingPlanRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(plans);
    }

    private BillingPlanResponse toResponse(BillingPlan plan) {
        return BillingPlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .displayName(plan.getDisplayName())
                .monthlyPrice(plan.getMonthlyPrice())
                .yearlyPrice(plan.getYearlyPrice())
                .requestLimit(plan.getRequestLimit())
                .overagePricePerRequest(plan.getOveragePricePerRequest())
                .features(plan.getFeatures())
                .isActive(plan.getIsActive())
                .build();
    }
}