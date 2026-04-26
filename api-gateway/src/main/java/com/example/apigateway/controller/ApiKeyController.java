package com.example.apigateway.controller;

import com.example.apigateway.dto.request.CreateApiKeyRequest;
import com.example.apigateway.dto.response.ApiKeyResponse;
import com.example.apigateway.entity.ApiKey;
import com.example.apigateway.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<ApiKeyResponse> createApiKey(
            @Valid @RequestBody CreateApiKeyRequest request) {
        ApiKey apiKey = apiKeyService.createApiKey(
                request.getName(),
                request.getOrganizationId(),
                request.getDailyRequestLimit(),
                request.getMonthlyRequestLimit(),
                request.getExpiresAt()
        );

        return ResponseEntity.ok(toResponse(apiKey));
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<java.util.List<ApiKeyResponse>> getOrganizationKeys(
            @PathVariable UUID organizationId) {
        return ResponseEntity.ok(
                apiKeyService.getOrganizationKeys(organizationId)
                        .stream()
                        .map(this::toResponse)
                        .collect(java.util.stream.Collectors.toList())
        );
    }

    @DeleteMapping("/{keyId}")
    public ResponseEntity<Void> deactivateApiKey(@PathVariable UUID keyId) {
        apiKeyService.deactivateApiKey(keyId);
        return ResponseEntity.noContent().build();
    }

    private ApiKeyResponse toResponse(ApiKey apiKey) {
        return ApiKeyResponse.builder()
                .id(apiKey.getId())
                .keyValue(apiKey.getKeyValue())
                .name(apiKey.getName())
                .organizationId(apiKey.getOrganizationId())
                .isActive(apiKey.getIsActive())
                .dailyRequestLimit(apiKey.getDailyRequestLimit())
                .monthlyRequestLimit(apiKey.getMonthlyRequestLimit())
                .createdAt(apiKey.getCreatedAt())
                .expiresAt(apiKey.getExpiresAt())
                .build();
    }
}