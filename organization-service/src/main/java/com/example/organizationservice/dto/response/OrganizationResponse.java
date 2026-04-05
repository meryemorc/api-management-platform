package com.example.organizationservice.dto.response;

import com.example.organizationservice.entity.PlanType;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class OrganizationResponse {
    private UUID id;
    private String name;
    private String slug;
    private PlanType plan;
    private Boolean isActive;
    private LocalDateTime createdAt;
}