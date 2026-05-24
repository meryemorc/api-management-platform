package com.example.notificationservice.repository;

import com.example.notificationservice.entity.WebhookSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WebhookSubscriptionRepository extends JpaRepository<WebhookSubscription, UUID> {
    List<WebhookSubscription> findByOrganizationIdAndIsActiveTrue(UUID organizationId);
}