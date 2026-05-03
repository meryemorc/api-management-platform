package com.example.organizationservice.controller;

import com.example.organizationservice.dto.request.AddMemberRequest;
import com.example.organizationservice.dto.request.CreateOrganizationRequest;
import com.example.organizationservice.dto.response.MemberResponse;
import com.example.organizationservice.dto.response.OrganizationResponse;
import com.example.organizationservice.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Tag(name = "Organization", description = "Organizasyon ve üye yönetimi")
public class OrganizationController {

    private final OrganizationService organizationService;

    @Operation(summary = "Organizasyon oluştur")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Organizasyon oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Bu slug zaten kullanımda")
    })
    @PostMapping
    public ResponseEntity<OrganizationResponse> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(organizationService.createOrganization(request, userId));
    }

    @Operation(summary = "Kullanıcının organizasyonlarını listele")
    @GetMapping("/my")
    public ResponseEntity<List<OrganizationResponse>> getMyOrganizations(
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(organizationService.getUserOrganizations(userId));
    }

    @Operation(summary = "Slug ile organizasyon getir")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Organizasyon bulundu"),
            @ApiResponse(responseCode = "404", description = "Organizasyon bulunamadı")
    })
    @GetMapping("/{slug}")
    public ResponseEntity<OrganizationResponse> getOrganization(@PathVariable String slug) {
        return ResponseEntity.ok(organizationService.getOrganizationBySlug(slug));
    }

    @Operation(summary = "Organizasyona üye ekle")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Üye eklendi"),
            @ApiResponse(responseCode = "403", description = "Yetkisiz işlem"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı veya organizasyon bulunamadı")
    })
    @PostMapping("/{slug}/members")
    public ResponseEntity<MemberResponse> addMember(
            @PathVariable String slug,
            @Valid @RequestBody AddMemberRequest request,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        return ResponseEntity.ok(organizationService.addMember(slug, request, userId));
    }

    @Operation(summary = "Organizasyon üyelerini listele")
    @GetMapping("/{slug}/members")
    public ResponseEntity<List<MemberResponse>> getMembers(@PathVariable String slug) {
        return ResponseEntity.ok(organizationService.getMembers(slug));
    }

    @Operation(summary = "Organizasyondan üye çıkar")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Üye çıkarıldı"),
            @ApiResponse(responseCode = "403", description = "Yetkisiz işlem"),
            @ApiResponse(responseCode = "404", description = "Üye bulunamadı")
    })
    @DeleteMapping("/{slug}/members/{targetUserId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable String slug,
            @PathVariable UUID targetUserId,
            @RequestHeader("X-User-Id") String userIdHeader) {
        UUID userId = UUID.fromString(userIdHeader);
        organizationService.removeMember(slug, targetUserId, userId);
        return ResponseEntity.noContent().build();
    }
}