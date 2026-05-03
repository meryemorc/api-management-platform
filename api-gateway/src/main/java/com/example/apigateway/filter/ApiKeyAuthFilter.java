package com.example.apigateway.filter;

import com.example.apigateway.service.ApiKeyService;
import com.example.apigateway.service.RateLimitService;
import com.example.apigateway.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthFilter implements GlobalFilter, Ordered {

    private final ApiKeyService apiKeyService;
    private final RateLimitService rateLimitService;
    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Auth endpoint'leri ve Swagger geç
        if (path.startsWith("/api/v1/auth")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")) {
            return chain.filter(exchange);
        }

        String apiKeyHeader = exchange.getRequest().getHeaders().getFirst("X-API-Key");
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        // API Key ile gelen istek
        if (apiKeyHeader != null && !apiKeyHeader.isEmpty()) {
            return handleApiKeyRequest(exchange, chain, apiKeyHeader);
        }

        // JWT ile gelen istek
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return handleJwtRequest(exchange, chain, authHeader);
        }

        // İkisi de yoksa reddet
        return unauthorized(exchange);
    }

    private Mono<Void> handleApiKeyRequest(ServerWebExchange exchange,
                                           GatewayFilterChain chain,
                                           String apiKeyHeader) {
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
                                // Downstream servislere organizasyon bilgisini iletiyoruz
                                // Bu sayede her servis kendi başına token parse etmek zorunda kalmıyor
                                ServerWebExchange mutatedExchange = exchange.mutate()
                                        .request(r -> r
                                                .header("X-Organization-Id", key.getOrganizationId().toString())
                                                .header("X-RateLimit-Remaining", String.valueOf(remaining))
                                                .header("X-RateLimit-Limit", String.valueOf(key.getDailyRequestLimit()))
                                        )
                                        .build();

                                return chain.filter(mutatedExchange);
                            });
                        })
                )
                // validateApiKey Mono.empty() döndürdüyse key geçersiz demektir
                .switchIfEmpty(Mono.defer(() -> unauthorized(exchange)));
    }

    private Mono<Void> handleJwtRequest(ServerWebExchange exchange,
                                        GatewayFilterChain chain,
                                        String authHeader) {
        String token = authHeader.substring(7);

        // Token geçerli değilse reddet — önceki kodda bu kontrol yoktu
        if (!jwtService.isTokenValid(token)) {
            return unauthorized(exchange);
        }

        String userId = jwtService.extractUserId(token);
        String email = jwtService.extractEmail(token);

        // Downstream servislere kullanıcı bilgisini header olarak iletiyoruz
        // Servisler token parse etmek yerine bu header'ları okuyabilir
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