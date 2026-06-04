package com.example.billingservice.repository;

import com.example.billingservice.entity.Subscription;
import com.example.billingservice.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findByOrganizationId(UUID organizationId);
    List<Subscription> findByStatus(SubscriptionStatus status);
    boolean existsByOrganizationId(UUID organizationId);
}