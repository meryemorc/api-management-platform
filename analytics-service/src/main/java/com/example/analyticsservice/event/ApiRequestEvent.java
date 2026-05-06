package com.example.analyticsservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiRequestEvent {
    private String apiKeyId;
    private UUID organizationId;
    private String path;
    private String method;
    private int statusCode;
    private long responseTimeMs;
    private LocalDateTime timestamp;
}