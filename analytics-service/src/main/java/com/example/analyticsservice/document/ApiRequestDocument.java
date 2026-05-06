package com.example.analyticsservice.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "api_requests")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiRequestDocument {

    @Id
    private String id;

    @Indexed
    private String organizationId;

    private String apiKeyId;
    private String path;
    private String method;
    private int statusCode;
    private long responseTimeMs;

    @Indexed
    private LocalDateTime timestamp;
}