package com.example.billingservice.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class BillingPlanResponse {
    private UUID id;
    private String name;
    private String displayName;
    private BigDecimal monthlyPrice;
    private BigDecimal yearlyPrice;
    private Integer requestLimit;
    private BigDecimal overagePricePerRequest;
    private String features;
    private Boolean isActive;
}