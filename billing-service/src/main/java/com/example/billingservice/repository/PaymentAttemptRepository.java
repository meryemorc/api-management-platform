package com.example.billingservice.repository;

import com.example.billingservice.entity.PaymentAttempt;
import com.example.billingservice.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentAttemptRepository extends JpaRepository<PaymentAttempt, UUID> {
    List<PaymentAttempt> findByInvoiceIdOrderByAttemptedAtDesc(UUID invoiceId);
    List<PaymentAttempt> findByOrganizationIdAndStatus(UUID organizationId, PaymentStatus status);
}