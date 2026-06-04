package com.example.billingservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangePlanRequest {

    @NotBlank(message = "Plan name is required")
    private String planName;
}