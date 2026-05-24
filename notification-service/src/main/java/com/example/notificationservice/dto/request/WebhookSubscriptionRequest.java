package com.example.notificationservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WebhookSubscriptionRequest {

    @NotBlank(message = "URL cannot be blank")
    @Pattern(regexp = "^https?://.*", message = "URL must start with http:// or https://")
    private String url;

    private String secret;

    @NotBlank(message = "Event types cannot be blank")
    private String eventTypes;
}