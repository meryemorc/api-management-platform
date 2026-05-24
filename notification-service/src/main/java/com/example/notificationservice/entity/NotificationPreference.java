package com.example.notificationservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification_preferences")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", nullable = false, unique = true)
    private UUID organizationId;

    @Column(name = "email_enabled")
    private Boolean emailEnabled = true;

    @Column(name = "webhook_enabled")
    private Boolean webhookEnabled = false;

    @Column(name = "rate_limit_warning_enabled")
    private Boolean rateLimitWarningEnabled = true;

    @Column(name = "rate_limit_exceeded_enabled")
    private Boolean rateLimitExceededEnabled = true;

    @Column(name = "api_key_expiring_enabled")
    private Boolean apiKeyExpiringEnabled = true;

    @Column(name = "daily_report_enabled")
    private Boolean dailyReportEnabled = false;

    @Column(name = "high_error_rate_enabled")
    private Boolean highErrorRateEnabled = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}