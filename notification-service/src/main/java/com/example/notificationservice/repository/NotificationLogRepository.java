package com.example.notificationservice.repository;

import com.example.notificationservice.entity.NotificationLog;
import com.example.notificationservice.enums.NotificationStatus;
import com.example.notificationservice.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {

    List<NotificationLog> findByOrganizationIdOrderByCreatedAtDesc(UUID organizationId);

    List<NotificationLog> findByOrganizationIdAndCreatedAtBetween(
            UUID organizationId, LocalDateTime start, LocalDateTime end);

    long countByOrganizationIdAndStatusAndCreatedAtAfter(
            UUID organizationId, NotificationStatus status, LocalDateTime after);

    boolean existsByOrganizationIdAndNotificationTypeAndCreatedAtAfter(
            UUID organizationId, NotificationType type, LocalDateTime after);
}