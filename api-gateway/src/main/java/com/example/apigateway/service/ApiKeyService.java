package com.example.apigateway.service;

import com.example.apigateway.entity.ApiKey;
import com.example.apigateway.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    public Mono<ApiKey> validateApiKey(String keyValue) {
        return apiKeyRepository.findByKeyValueAndIsActiveTrue(keyValue)
                .filter(key -> key.getExpiresAt() == null
                        || key.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    public Mono<ApiKey> createApiKey(String name, UUID organizationId,
                                     Integer dailyLimit, Integer monthlyLimit,
                                     LocalDateTime expiresAt) {
        ApiKey apiKey = ApiKey.builder()
                .keyValue(generateKeyValue())
                .name(name)
                .organizationId(organizationId)
                .isActive(true)
                .dailyRequestLimit(dailyLimit)
                .monthlyRequestLimit(monthlyLimit)
                .createdAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .build();

        return apiKeyRepository.save(apiKey);
    }

    public Flux<ApiKey> getOrganizationKeys(UUID organizationId) {
        return apiKeyRepository.findByOrganizationId(organizationId);
    }

    public Mono<Void> deactivateApiKey(UUID keyId) {
        return apiKeyRepository.findById(keyId)
                .switchIfEmpty(Mono.error(new RuntimeException("API key bulunamadı: " + keyId)))
                .flatMap(key -> {
                    key.setIsActive(false);
                    return apiKeyRepository.save(key);
                })
                .then();
    }

    private String generateKeyValue() {
        return "pk_live_" + UUID.randomUUID().toString().replace("-", "");
    }
}