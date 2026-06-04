package com.example.billingservice.repository;

import com.example.billingservice.entity.BillingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BillingPlanRepository extends JpaRepository<BillingPlan, UUID> {
    Optional<BillingPlan> findByName(String name);
    Optional<BillingPlan> findByNameAndIsActiveTrue(String name);
}