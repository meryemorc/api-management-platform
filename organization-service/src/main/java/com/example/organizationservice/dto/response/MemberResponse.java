package com.example.organizationservice.dto.response;

import com.example.organizationservice.entity.MemberRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class MemberResponse {
    private UUID id;
    private UUID userId;
    private MemberRole role;
    private LocalDateTime joinedAt;
}