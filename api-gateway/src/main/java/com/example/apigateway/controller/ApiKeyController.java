package com.example.apigateway.controller;

import com.example.apigateway.dto.request.CreateApiKeyRequest;
import com.example.apigateway.dto.response.ApiKeyResponse;
import com.example.apigateway.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * TODO: Bu controller ileride Organization Service'e taşınacak.
 * API key yönetimi iş mantığı olarak orada olmalı.
 * Gateway sadece key doğrulaması yapmalı.
 */
@RestController
@RequestMapping("/api/v1/keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    public Mono<ResponseEntity<ApiKeyResponse>> createApiKey(
            @Valid @RequestBody CreateApiKeyRequest request) {
        return apiKeyService.createApiKey(
                        request.getName(),
                        request.getOrganizationId(),
                        request.getDailyRequestLimit(),
                        request.getMonthlyRequestLimit(),
                        request.getExpiresAt()
                )
                .map(this::toResponse)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/organization/{organizationId}")
    public Flux<ApiKeyResponse> getOrganizationKeys(@PathVariable UUID organizationId) {
        return apiKeyService.getOrganizationKeys(organizationId)
                .map(this::toResponse);
    }

    @DeleteMapping("/{keyId}")
    public Mono<ResponseEntity<Void>> deactivateApiKey(@PathVariable UUID keyId) {
        return apiKeyService.deactivateApiKey(keyId)
                .thenReturn(ResponseEntity.<Void>noContent().build());
    }

    private ApiKeyResponse toResponse(com.example.apigateway.entity.ApiKey apiKey) {
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