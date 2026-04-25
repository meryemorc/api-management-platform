package com.example.apigateway.service;

import com.example.apigateway.entity.ApiKey;
import com.example.apigateway.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    public Optional<ApiKey> validateApiKey(String keyValue) {
        Optional<ApiKey> apiKey = apiKeyRepository.findByKeyValueAndIsActiveTrue(keyValue);

        if (apiKey.isPresent()) {
            ApiKey key = apiKey.get();
            // Key süresi dolmuş mu kontrol et
            if (key.getExpiresAt() != null && key.getExpiresAt().isBefore(LocalDateTime.now())) {
                return Optional.empty();
            }
        }

        return apiKey;
    }

    public ApiKey createApiKey(String name, UUID organizationId,
                               Integer dailyLimit, Integer monthlyLimit,
                               LocalDateTime expiresAt) {
        ApiKey apiKey = ApiKey.builder()
                .keyValue(generateKeyValue())
                .name(name)
                .organizationId(organizationId)
                .isActive(true)
                .dailyRequestLimit(dailyLimit)
                .monthlyRequestLimit(monthlyLimit)
                .expiresAt(expiresAt)
                .build();

        return apiKeyRepository.save(apiKey);
    }

    private String generateKeyValue() {
        return "pk_live_" + UUID.randomUUID().toString().replace("-", "");
    }
}