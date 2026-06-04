package com.example.billingservice.dto.response;

import com.example.billingservice.enums.InvoiceStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class InvoiceResponse {
    private UUID id;
    private UUID organizationId;
    private String invoiceNumber;
    private InvoiceStatus status;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;
    private String currency;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private LocalDateTime dueDate;
    private LocalDateTime paidAt;
    private List<InvoiceItemResponse> items;
    private LocalDateTime createdAt;
}