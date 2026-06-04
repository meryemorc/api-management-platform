package com.example.billingservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
public class StripeService {

    public String createCustomer(UUID organizationId, String email) {
        // Gerçekte: Stripe API'ye istek at
        String customerId = "cus_sim_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);
        log.info("Simulated Stripe customer created: orgId={}, customerId={}", organizationId, customerId);
        return customerId;
    }

    public String createSubscription(String customerId, String planId, String billingCycle) {
        // Gerçekte: Stripe subscription oluştur
        String subscriptionId = "sub_sim_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);
        log.info("Simulated Stripe subscription created: customerId={}, subscriptionId={}", customerId, subscriptionId);
        return subscriptionId;
    }

    public PaymentResult charge(String customerId, BigDecimal amount, String currency, String description) {
        // Gerçekte: Stripe payment intent oluştur ve onayla
        String paymentIntentId = "pi_sim_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);
        log.info("Simulated Stripe charge: customerId={}, amount={} {}", customerId, amount, currency);

        // %95 başarı oranı simülasyonu
        if (Math.random() < 0.95) {
            return PaymentResult.success(paymentIntentId);
        } else {
            return PaymentResult.failure(paymentIntentId, "Simulated payment failure");
        }
    }

    public String createInvoice(String customerId, BigDecimal amount, String currency) {
        String invoiceId = "in_sim_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);
        log.info("Simulated Stripe invoice created: customerId={}, amount={}", customerId, amount);
        return invoiceId;
    }

    public boolean cancelSubscription(String stripeSubscriptionId) {
        log.info("Simulated Stripe subscription cancelled: {}", stripeSubscriptionId);
        return true;
    }

    // Inner class — ödeme sonucu
    public static class PaymentResult {
        private final boolean success;
        private final String paymentIntentId;
        private final String errorMessage;

        private PaymentResult(boolean success, String paymentIntentId, String errorMessage) {
            this.success = success;
            this.paymentIntentId = paymentIntentId;
            this.errorMessage = errorMessage;
        }

        public static PaymentResult success(String paymentIntentId) {
            return new PaymentResult(true, paymentIntentId, null);
        }

        public static PaymentResult failure(String paymentIntentId, String errorMessage) {
            return new PaymentResult(false, paymentIntentId, errorMessage);
        }

        public boolean isSuccess() { return success; }
        public String getPaymentIntentId() { return paymentIntentId; }
        public String getErrorMessage() { return errorMessage; }
    }
}