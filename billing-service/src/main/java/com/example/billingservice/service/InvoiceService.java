package com.example.billingservice.service;

import com.example.billingservice.entity.*;
import com.example.billingservice.enums.InvoiceItemType;
import com.example.billingservice.enums.InvoiceStatus;
import com.example.billingservice.enums.PaymentStatus;
import com.example.billingservice.exception.ResourceNotFoundException;
import com.example.billingservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UsageRecordRepository usageRecordRepository;
    private final PaymentAttemptRepository paymentAttemptRepository;
    private final StripeService stripeService;

    private static final AtomicLong invoiceCounter = new AtomicLong(1000);
    private static final double TAX_RATE = 0.18; // %18 KDV

    @Transactional
    public Invoice generateMonthlyInvoice(UUID organizationId) {
        Subscription subscription = subscriptionRepository
                .findByOrganizationId(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subscription not found: " + organizationId));

        LocalDateTime periodStart = subscription.getCurrentPeriodStart();
        LocalDateTime periodEnd = subscription.getCurrentPeriodEnd();

        List<InvoiceItem> items = new ArrayList<>();

        // Plan ücreti
        BigDecimal planPrice = subscription.getPlan().getMonthlyPrice();
        InvoiceItem planItem = InvoiceItem.builder()
                .description(subscription.getPlan().getDisplayName() + " - Monthly Subscription")
                .quantity(1L)
                .unitPrice(planPrice)
                .amount(planPrice)
                .type(InvoiceItemType.SUBSCRIPTION)
                .build();
        items.add(planItem);

        // Overage ücreti
        UsageRecord usageRecord = usageRecordRepository
                .findByOrganizationIdAndPeriodStartAndPeriodEnd(
                        organizationId, periodStart, periodEnd)
                .orElse(null);

        if (usageRecord != null && usageRecord.getOverageRequests() > 0) {
            InvoiceItem overageItem = InvoiceItem.builder()
                    .description("API Overage - " + usageRecord.getOverageRequests() + " extra requests")
                    .quantity(usageRecord.getOverageRequests())
                    .unitPrice(subscription.getPlan().getOveragePricePerRequest())
                    .amount(usageRecord.getOverageAmount())
                    .type(InvoiceItemType.OVERAGE)
                    .build();
            items.add(overageItem);
        }

        // Toplam hesapla
        BigDecimal subtotal = items.stream()
                .map(InvoiceItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(TAX_RATE))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax);

        // Stripe'a fatura oluştur
        String stripeInvoiceId = stripeService.createInvoice(
                subscription.getStripeCustomerId(), total, "USD");

        Invoice invoice = Invoice.builder()
                .organizationId(organizationId)
                .subscription(subscription)
                .invoiceNumber(generateInvoiceNumber())
                .status(InvoiceStatus.OPEN)
                .subtotal(subtotal)
                .tax(tax)
                .total(total)
                .currency("USD")
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .dueDate(LocalDateTime.now().plusDays(30))
                .stripeInvoiceId(stripeInvoiceId)
                .items(items)
                .build();

        items.forEach(item -> item.setInvoice(invoice));
        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice generated: orgId={}, invoiceNumber={}, total={}",
                organizationId, saved.getInvoiceNumber(), total);
        return saved;
    }

    @Transactional
    public Invoice processPayment(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));

        String stripeCustomerId = invoice.getSubscription().getStripeCustomerId();
        StripeService.PaymentResult result = stripeService.charge(
                stripeCustomerId, invoice.getTotal(), invoice.getCurrency(),
                "Invoice " + invoice.getInvoiceNumber());

        PaymentAttempt attempt = PaymentAttempt.builder()
                .invoice(invoice)
                .organizationId(invoice.getOrganizationId())
                .amount(invoice.getTotal())
                .currency(invoice.getCurrency())
                .status(result.isSuccess() ? PaymentStatus.SUCCEEDED : PaymentStatus.FAILED)
                .stripePaymentIntentId(result.getPaymentIntentId())
                .failureMessage(result.getErrorMessage())
                .attemptedAt(LocalDateTime.now())
                .build();

        paymentAttemptRepository.save(attempt);

        if (result.isSuccess()) {
            invoice.setStatus(InvoiceStatus.PAID);
            invoice.setPaidAt(LocalDateTime.now());
            invoice.setStripePaymentIntentId(result.getPaymentIntentId());
            log.info("Payment succeeded: invoiceId={}, amount={}", invoiceId, invoice.getTotal());
        } else {
            invoice.setStatus(InvoiceStatus.OPEN);
            log.warn("Payment failed: invoiceId={}, reason={}", invoiceId, result.getErrorMessage());
        }

        return invoiceRepository.save(invoice);
    }

    public List<Invoice> getInvoicesByOrganization(UUID organizationId) {
        return invoiceRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId);
    }

    public Invoice getInvoiceById(UUID invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));
    }

    private String generateInvoiceNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        return "INV-" + date + "-" + invoiceCounter.getAndIncrement();
    }
    public List<Invoice> getOverdueInvoices() {
        return invoiceRepository.findByStatusAndDueDateBefore(
                InvoiceStatus.OPEN, LocalDateTime.now());
    }
}