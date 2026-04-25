package com.example.apigateway.filter;

import com.example.apigateway.service.ApiKeyService;
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

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Auth endpoint'leri API key gerektirmez
        if (path.startsWith("/api/v1/auth")) {
            return chain.filter(exchange);
        }

        String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-Key");

        // API key yoksa JWT token kontrolü yap
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
                    return chain.filter(exchange);
                });
    }

    @Override
    public int getOrder() {
        return -1; // En yüksek öncelik
    }
}