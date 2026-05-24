package com.example.billingservice.repository;

import com.example.billingservice.entity.UsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsageRecordRepository extends JpaRepository<UsageRecord, UUID> {

    Optional<UsageRecord> findByOrganizationIdAndPeriodStartAndPeriodEnd(
            UUID organizationId, LocalDateTime periodStart, LocalDateTime periodEnd);

    List<UsageRecord> findByOrganizationIdOrderByPeriodStartDesc(UUID organizationId);

    @Query("SELECT SUM(u.totalRequests) FROM UsageRecord u " +
            "WHERE u.organizationId = :organizationId " +
            "AND u.periodStart >= :start AND u.periodEnd <= :end")
    Long sumTotalRequestsByOrganizationIdAndPeriod(
            UUID organizationId, LocalDateTime start, LocalDateTime end);
}