package com.example.billingservice.dto.response;

import com.example.billingservice.enums.InvoiceItemType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class InvoiceItemResponse {
    private UUID id;
    private String description;
    private Long quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private InvoiceItemType type;
}