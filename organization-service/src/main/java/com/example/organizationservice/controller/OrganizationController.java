package com.example.organizationservice.controller;

import com.example.organizationservice.dto.request.CreateOrganizationRequest;
import com.example.organizationservice.dto.response.OrganizationResponse;
import com.example.organizationservice.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    public ResponseEntity<OrganizationResponse> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString((String) authentication.getCredentials());
        return ResponseEntity.ok(organizationService.createOrganization(request, userId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrganizationResponse>> getMyOrganizations(Authentication authentication) {
        UUID userId = UUID.fromString((String) authentication.getCredentials());
        return ResponseEntity.ok(organizationService.getUserOrganizations(userId));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<OrganizationResponse> getOrganization(@PathVariable String slug) {
        return ResponseEntity.ok(organizationService.getOrganizationBySlug(slug));
    }
}