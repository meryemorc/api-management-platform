package com.example.organizationservice.service;

import com.example.organizationservice.dto.request.AddMemberRequest;
import com.example.organizationservice.dto.request.CreateOrganizationRequest;
import com.example.organizationservice.dto.response.MemberResponse;
import com.example.organizationservice.dto.response.OrganizationResponse;
import com.example.organizationservice.entity.MemberRole;
import com.example.organizationservice.entity.Organization;
import com.example.organizationservice.entity.OrganizationMember;
import com.example.organizationservice.entity.PlanType;
import com.example.organizationservice.exception.DuplicateResourceException;
import com.example.organizationservice.exception.ResourceNotFoundException;
import com.example.organizationservice.exception.UnauthorizedException;
import com.example.organizationservice.repository.OrganizationMemberRepository;
import com.example.organizationservice.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final RestTemplate restTemplate;

    @Value("${services.user-service.url}")
    private String userServiceUrl;

    @Transactional
    public OrganizationResponse createOrganization(CreateOrganizationRequest request, UUID userId) {
        if (organizationRepository.existsBySlug(request.getSlug())) {
            throw new DuplicateResourceException("Bu slug zaten kullanımda: " + request.getSlug());
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
        return organizationMemberRepository.findByUserId(userId)
                .stream()
                .map(m -> toResponse(m.getOrganization()))
                .collect(Collectors.toList());
    }

    public OrganizationResponse getOrganizationBySlug(String slug) {
        Organization org = organizationRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Organizasyon bulunamadı: " + slug));
        return toResponse(org);
    }

    @Transactional
    public MemberResponse addMember(String slug, AddMemberRequest request, UUID requestingUserId) {
        Organization organization = organizationRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Organizasyon bulunamadı: " + slug));

        OrganizationMember requestingMember = organizationMemberRepository
                .findByUserIdAndOrganizationId(requestingUserId, organization.getId())
                .orElseThrow(() -> new UnauthorizedException("Bu organizasyona erişim yetkiniz yok"));

        if (requestingMember.getRole() != MemberRole.ORG_ADMIN) {
            throw new UnauthorizedException("Üye eklemek için ORG_ADMIN olmanız gerekiyor");
        }

        // User Service'ten email ile userId al
        String url = userServiceUrl + "/api/v1/users/by-email/" + request.getEmail();
        Map response = restTemplate.getForObject(url, Map.class);

        if (response == null) {
            throw new ResourceNotFoundException("Kullanıcı bulunamadı: " + request.getEmail());
        }

        UUID targetUserId = UUID.fromString((String) response.get("userId"));

        if (organizationMemberRepository.existsByUserIdAndOrganizationId(targetUserId, organization.getId())) {
            throw new DuplicateResourceException("Bu kullanıcı zaten organizasyonun üyesi");
        }

        MemberRole role;
        try {
            role = MemberRole.valueOf(request.getRole());
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Geçersiz rol: " + request.getRole());
        }

        OrganizationMember member = OrganizationMember.builder()
                .userId(targetUserId)
                .organization(organization)
                .role(role)
                .build();

        organizationMemberRepository.save(member);
        return toMemberResponse(member);
    }

    public List<MemberResponse> getMembers(String slug) {
        Organization organization = organizationRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Organizasyon bulunamadı: " + slug));

        return organizationMemberRepository.findByOrganizationId(organization.getId())
                .stream()
                .map(this::toMemberResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeMember(String slug, UUID targetUserId, UUID requestingUserId) {
        Organization organization = organizationRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Organizasyon bulunamadı: " + slug));

        OrganizationMember requestingMember = organizationMemberRepository
                .findByUserIdAndOrganizationId(requestingUserId, organization.getId())
                .orElseThrow(() -> new UnauthorizedException("Bu organizasyona erişim yetkiniz yok"));

        if (requestingMember.getRole() != MemberRole.ORG_ADMIN) {
            throw new UnauthorizedException("Üye çıkarmak için ORG_ADMIN olmanız gerekiyor");
        }

        OrganizationMember targetMember = organizationMemberRepository
                .findByUserIdAndOrganizationId(targetUserId, organization.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Üye bulunamadı"));

        organizationMemberRepository.delete(targetMember);
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

    private MemberResponse toMemberResponse(OrganizationMember member) {
        return MemberResponse.builder()
                .id(member.getId())
                .userId(member.getUserId())
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}