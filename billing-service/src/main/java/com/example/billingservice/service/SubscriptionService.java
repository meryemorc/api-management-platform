package com.example.billingservice.service;

import com.example.billingservice.entity.BillingPlan;
import com.example.billingservice.entity.Subscription;
import com.example.billingservice.enums.BillingCycle;
import com.example.billingservice.enums.SubscriptionStatus;
import com.example.billingservice.exception.BillingException;
import com.example.billingservice.exception.ResourceNotFoundException;
import com.example.billingservice.repository.BillingPlanRepository;
import com.example.billingservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final BillingPlanRepository billingPlanRepository;
    private final StripeService stripeService;

    @Transactional
    public Subscription createSubscription(UUID organizationId, String planName, BillingCycle billingCycle) {
        if (subscriptionRepository.existsByOrganizationId(organizationId)) {
            throw new BillingException("Subscription already exists for organization: " + organizationId);
        }

        BillingPlan plan = billingPlanRepository.findByNameAndIsActiveTrue(planName)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found: " + planName));

        String stripeCustomerId = stripeService.createCustomer(organizationId, "admin@organization.com");
        String stripeSubscriptionId = stripeService.createSubscription(
                stripeCustomerId, plan.getId().toString(), billingCycle.name());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime periodEnd = billingCycle == BillingCycle.MONTHLY
                ? now.plusMonths(1) : now.plusYears(1);

        Subscription subscription = Subscription.builder()
                .organizationId(organizationId)
                .plan(plan)
                .status(SubscriptionStatus.ACTIVE)
                .billingCycle(billingCycle)
                .currentPeriodStart(now)
                .currentPeriodEnd(periodEnd)
                .cancelAtPeriodEnd(false)
                .stripeCustomerId(stripeCustomerId)
                .stripeSubscriptionId(stripeSubscriptionId)
                .build();

        Subscription saved = subscriptionRepository.save(subscription);
        log.info("Subscription created: orgId={}, plan={}", organizationId, planName);
        return saved;
    }

    public Subscription getByOrganizationId(UUID organizationId) {
        return subscriptionRepository.findByOrganizationId(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subscription not found for organization: " + organizationId));
    }

    @Transactional
    public Subscription changePlan(UUID organizationId, String newPlanName) {
        Subscription subscription = getByOrganizationId(organizationId);
        BillingPlan newPlan = billingPlanRepository.findByNameAndIsActiveTrue(newPlanName)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found: " + newPlanName));
        subscription.setPlan(newPlan);
        Subscription saved = subscriptionRepository.save(subscription);
        log.info("Plan changed: orgId={}, newPlan={}", organizationId, newPlanName);
        return saved;
    }

    @Transactional
    public Subscription cancelSubscription(UUID organizationId) {
        Subscription subscription = getByOrganizationId(organizationId);
        subscription.setCancelAtPeriodEnd(true);
        stripeService.cancelSubscription(subscription.getStripeSubscriptionId());
        Subscription saved = subscriptionRepository.save(subscription);
        log.info("Subscription cancelled at period end: orgId={}", organizationId);
        return saved;
    }
}