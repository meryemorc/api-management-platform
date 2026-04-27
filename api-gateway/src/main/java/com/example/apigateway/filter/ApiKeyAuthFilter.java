package com.example.apigateway.filter;

import com.example.apigateway.entity.ApiKey;
import com.example.apigateway.service.ApiKeyService;
import com.example.apigateway.service.RateLimitService;
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

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Auth endpoint'leri geç
        if (path.startsWith("/api/v1/auth")) {
            return chain.filter(exchange);
        }

        String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-Key");

        // API key yoksa JWT kontrolü yap
        if (apiKey == null || apiKey.isEmpty()) {
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return chain.filter(exchange);
            }
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // API key doğrula
        return Mono.fromCallable(() -> apiKeyService.validateApiKey(apiKey))
                .flatMap(validKey -> {
                    if (validKey.isEmpty()) {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }

                    ApiKey key = validKey.get();

                    // Rate limit kontrolü
                    return rateLimitService.isAllowed(
                            key.getOrganizationId(),
                            key.getId().toString(),
                            key.getDailyRequestLimit()
                    ).flatMap(allowed -> {
                        if (!allowed) {
                            // Limit aşıldı
                            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                            return exchange.getResponse().setComplete();
                        }

                        // Kalan istek sayısını header'a ekle
                        return rateLimitService.getRemainingRequests(
                                key.getId().toString(),
                                key.getDailyRequestLimit()
                        ).flatMap(remaining -> {
                            exchange.getResponse().getHeaders()
                                    .add("X-RateLimit-Remaining", String.valueOf(remaining));
                            exchange.getResponse().getHeaders()
                                    .add("X-RateLimit-Limit", String.valueOf(key.getDailyRequestLimit()));
                            return chain.filter(exchange);
                        });
                    });
                });
    }

    @Override
    public int getOrder() {
        return -1;
    }
}