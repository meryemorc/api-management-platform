package com.example.billingservice.controller;

import com.example.billingservice.dto.response.InvoiceItemResponse;
import com.example.billingservice.dto.response.InvoiceResponse;
import com.example.billingservice.entity.Invoice;
import com.example.billingservice.entity.InvoiceItem;
import com.example.billingservice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/{organizationId}")
    public ResponseEntity<List<InvoiceResponse>> getInvoices(
            @PathVariable UUID organizationId) {
        List<InvoiceResponse> invoices = invoiceService
                .getInvoicesByOrganization(organizationId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/detail/{invoiceId}")
    public ResponseEntity<InvoiceResponse> getInvoice(
            @PathVariable UUID invoiceId) {
        return ResponseEntity.ok(toResponse(invoiceService.getInvoiceById(invoiceId)));
    }

    @PostMapping("/{organizationId}/generate")
    public ResponseEntity<InvoiceResponse> generateInvoice(
            @PathVariable UUID organizationId) {
        Invoice invoice = invoiceService.generateMonthlyInvoice(organizationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(invoice));
    }

    @PostMapping("/{invoiceId}/pay")
    public ResponseEntity<InvoiceResponse> payInvoice(
            @PathVariable UUID invoiceId) {
        Invoice invoice = invoiceService.processPayment(invoiceId);
        return ResponseEntity.ok(toResponse(invoice));
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .organizationId(invoice.getOrganizationId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .status(invoice.getStatus())
                .subtotal(invoice.getSubtotal())
                .tax(invoice.getTax())
                .total(invoice.getTotal())
                .currency(invoice.getCurrency())
                .periodStart(invoice.getPeriodStart())
                .periodEnd(invoice.getPeriodEnd())
                .dueDate(invoice.getDueDate())
                .paidAt(invoice.getPaidAt())
                .items(invoice.getItems().stream().map(this::toItemResponse).toList())
                .createdAt(invoice.getCreatedAt())
                .build();
    }

    private InvoiceItemResponse toItemResponse(InvoiceItem item) {
        return InvoiceItemResponse.builder()
                .id(item.getId())
                .description(item.getDescription())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .amount(item.getAmount())
                .type(item.getType())
                .build();
    }
}