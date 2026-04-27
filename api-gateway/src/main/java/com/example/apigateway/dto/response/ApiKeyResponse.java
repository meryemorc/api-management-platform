package com.example.apigateway.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ApiKeyResponse {
    private UUID id;
    private String keyValue;
    private String name;
    private UUID organizationId;
    private Boolean isActive;
    private Integer dailyRequestLimit;
    private Integer monthlyRequestLimit;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}