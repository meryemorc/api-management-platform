package com.example.apigateway.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateApiKeyRequest {

    @NotBlank
    private String name;

    @NotNull
    private UUID organizationId;

    @NotNull
    @Positive
    private Integer dailyRequestLimit;

    @NotNull
    @Positive
    private Integer monthlyRequestLimit;

    private LocalDateTime expiresAt;
}