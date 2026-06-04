package com.example.billingservice.service;

import com.example.billingservice.entity.Subscription;
import com.example.billingservice.enums.SubscriptionStatus;
import com.example.billingservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingSchedulerService {

    private final SubscriptionRepository subscriptionRepository;
    private final InvoiceService invoiceService;

    // Her ayın 1'inde 00:00'da fatura oluştur
    @Scheduled(cron = "0 0 0 1 * *")
    public void generateMonthlyInvoices() {
        log.info("Monthly invoice generation started");

        List<Subscription> activeSubscriptions = subscriptionRepository
                .findByStatus(SubscriptionStatus.ACTIVE);

        activeSubscriptions.forEach(subscription -> {
            try {
                invoiceService.generateMonthlyInvoice(subscription.getOrganizationId());
                log.info("Invoice generated for org: {}", subscription.getOrganizationId());
            } catch (Exception e) {
                log.error("Failed to generate invoice for org: {}, error: {}",
                        subscription.getOrganizationId(), e.getMessage());
            }
        });

        log.info("Monthly invoice generation completed. Total: {}", activeSubscriptions.size());
    }

    // Her gün 09:00'da vadesi geçmiş faturaları kontrol et
    @Scheduled(cron = "0 0 9 * * *")
    public void processOverdueInvoices() {
        log.info("Overdue invoice check started");

        var overdueInvoices = invoiceService.getOverdueInvoices();

        overdueInvoices.forEach(invoice -> {
            try {
                invoiceService.processPayment(invoice.getId());
                log.info("Payment processed for overdue invoice: {}", invoice.getInvoiceNumber());
            } catch (Exception e) {
                log.error("Failed to process payment for invoice: {}, error: {}",
                        invoice.getInvoiceNumber(), e.getMessage());
            }
        });

        log.info("Overdue invoice check completed. Total: {}", overdueInvoices.size());
    }

    // Her gün gece yarısı expire olan subscription'ları kontrol et
    @Scheduled(cron = "0 0 0 * * *")
    public void checkExpiredSubscriptions() {
        log.info("Expired subscription check started");

        List<Subscription> activeSubscriptions = subscriptionRepository
                .findByStatus(SubscriptionStatus.ACTIVE);

        LocalDateTime now = LocalDateTime.now();
        activeSubscriptions.stream()
                .filter(s -> s.getCancelAtPeriodEnd() && s.getCurrentPeriodEnd().isBefore(now))
                .forEach(subscription -> {
                    subscription.setStatus(SubscriptionStatus.CANCELLED);
                    subscriptionRepository.save(subscription);
                    log.info("Subscription cancelled: orgId={}", subscription.getOrganizationId());
                });
    }
}