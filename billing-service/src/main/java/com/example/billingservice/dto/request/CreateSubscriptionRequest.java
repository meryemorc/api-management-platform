package com.example.billingservice.dto.request;

import com.example.billingservice.enums.BillingCycle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateSubscriptionRequest {

    @NotBlank(message = "Plan name is required")
    private String planName;

    @NotNull(message = "Billing cycle is required")
    private BillingCycle billingCycle;
}