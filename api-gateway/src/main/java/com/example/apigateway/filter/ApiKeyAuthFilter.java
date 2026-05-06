package com.example.apigateway.filter;

import com.example.apigateway.event.ApiRequestEvent;
import com.example.apigateway.service.ApiKeyService;
import com.example.apigateway.service.JwtService;
import com.example.apigateway.service.KafkaProducerService;
import com.example.apigateway.service.RateLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthFilter implements GlobalFilter, Ordered {

    private final ApiKeyService apiKeyService;
    private final RateLimitService rateLimitService;
    private final JwtService jwtService;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (path.startsWith("/api/v1/auth")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")) {
            return chain.filter(exchange);
        }

        String apiKeyHeader = exchange.getRequest().getHeaders().getFirst("X-API-Key");
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (apiKeyHeader != null && !apiKeyHeader.isEmpty()) {
            return handleApiKeyRequest(exchange, chain, apiKeyHeader);
        }

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return handleJwtRequest(exchange, chain, authHeader);
        }

        return unauthorized(exchange);
    }

    private Mono<Void> handleApiKeyRequest(ServerWebExchange exchange,
                                           GatewayFilterChain chain,
                                           String apiKeyHeader) {
        long startTime = System.currentTimeMillis();

        return apiKeyService.validateApiKey(apiKeyHeader)
                .flatMap(key ->
                        rateLimitService.isAllowed(
                                key.getOrganizationId(),
                                key.getId().toString(),
                                key.getDailyRequestLimit()
                        ).flatMap(allowed -> {
                            if (!allowed) {
                                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                                return exchange.getResponse().setComplete();
                            }

                            return rateLimitService.getRemainingRequests(
                                    key.getId().toString(),
                                    key.getDailyRequestLimit()
                            ).flatMap(remaining -> {
                                ServerWebExchange mutatedExchange = exchange.mutate()
                                        .request(r -> r
                                                .header("X-Organization-Id", key.getOrganizationId().toString())
                                                .header("X-RateLimit-Remaining", String.valueOf(remaining))
                                                .header("X-RateLimit-Limit", String.valueOf(key.getDailyRequestLimit()))
                                        )
                                        .build();

                                // Kafka event'i istek tamamlandıktan sonra gönderiyoruz
                                // doFinally hem başarılı hem hatalı durumda çalışır
                                return chain.filter(mutatedExchange)
                                        .doFinally(signalType -> {
                                            long responseTime = System.currentTimeMillis() - startTime;
                                            int statusCode = mutatedExchange.getResponse().getStatusCode() != null
                                                    ? mutatedExchange.getResponse().getStatusCode().value()
                                                    : 0;

                                            kafkaProducerService.sendApiRequestEvent(
                                                    ApiRequestEvent.builder()
                                                            .apiKeyId(key.getId().toString())
                                                            .organizationId(key.getOrganizationId())
                                                            .path(exchange.getRequest().getURI().getPath())
                                                            .method(exchange.getRequest().getMethod().name())
                                                            .statusCode(statusCode)
                                                            .responseTimeMs(responseTime)
                                                            .timestamp(LocalDateTime.now())
                                                            .build()
                                            );
                                        });
                            });
                        })
                )
                .switchIfEmpty(Mono.defer(() -> unauthorized(exchange)));
    }

    private Mono<Void> handleJwtRequest(ServerWebExchange exchange,
                                        GatewayFilterChain chain,
                                        String authHeader) {
        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            return unauthorized(exchange);
        }

        String userId = jwtService.extractUserId(token);
        String email = jwtService.extractEmail(token);

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(r -> r
                        .header("X-User-Id", userId)
                        .header("X-User-Email", email)
                )
                .build();

        return chain.filter(mutatedExchange);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}