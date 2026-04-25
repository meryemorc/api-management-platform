package com.example.apigateway.repository;

import com.example.apigateway.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    Optional<ApiKey> findByKeyValueAndIsActiveTrue(String keyValue);
}