package com.example.apigateway.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "api_keys")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "key_value", nullable = false, unique = true)
    private String keyValue;

    @Column(nullable = false)
    private String name;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "daily_request_limit", nullable = false)
    private Integer dailyRequestLimit;

    @Column(name = "monthly_request_limit", nullable = false)
    private Integer monthlyRequestLimit;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}