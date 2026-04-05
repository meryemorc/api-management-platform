package com.example.organizationservice.service;

import com.example.organizationservice.dto.request.CreateOrganizationRequest;
import com.example.organizationservice.dto.response.OrganizationResponse;
import com.example.organizationservice.entity.MemberRole;
import com.example.organizationservice.entity.Organization;
import com.example.organizationservice.entity.OrganizationMember;
import com.example.organizationservice.entity.PlanType;
import com.example.organizationservice.repository.OrganizationMemberRepository;
import com.example.organizationservice.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    @Transactional
    public OrganizationResponse createOrganization(CreateOrganizationRequest request, UUID userId) {
        if (organizationRepository.existsBySlug(request.getSlug())) {
            throw new RuntimeException("Bu slug zaten kullanımda");
        }

        Organization organization = Organization.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .plan(PlanType.FREE)
                .isActive(true)
                .build();

        organizationRepository.save(organization);

        OrganizationMember member = OrganizationMember.builder()
                .userId(userId)
                .organization(organization)
                .role(MemberRole.ORG_ADMIN)
                .build();

        organizationMemberRepository.save(member);

        return toResponse(organization);
    }

    public List<OrganizationResponse> getUserOrganizations(UUID userId) {
        return organizationMemberRepository.findByOrganizationId(userId)
                .stream()
                .map(m -> toResponse(m.getOrganization()))
                .collect(Collectors.toList());
    }

    public OrganizationResponse getOrganizationBySlug(String slug) {
        Organization org = organizationRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Organizasyon bulunamadı"));
        return toResponse(org);
    }

    private OrganizationResponse toResponse(Organization org) {
        return OrganizationResponse.builder()
                .id(org.getId())
                .name(org.getName())
                .slug(org.getSlug())
                .plan(org.getPlan())
                .isActive(org.getIsActive())
                .createdAt(org.getCreatedAt())
                .build();
    }
}