package com.example.apigateway.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("api_keys")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKey {

    @Id
    private UUID id;

    @Column("key_value")
    private String keyValue;

    @Column("name")
    private String name;

    @Column("organization_id")
    private UUID organizationId;

    @Column("is_active")
    private Boolean isActive;

    @Column("daily_request_limit")
    private Integer dailyRequestLimit;

    @Column("monthly_request_limit")
    private Integer monthlyRequestLimit;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("expires_at")
    private LocalDateTime expiresAt;
}