package com.example.apigateway.repository;

import com.example.apigateway.entity.ApiKey;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface ApiKeyRepository extends ReactiveCrudRepository<ApiKey, UUID> {
    Mono<ApiKey> findByKeyValueAndIsActiveTrue(String keyValue);
    Flux<ApiKey> findByOrganizationId(UUID organizationId);
}