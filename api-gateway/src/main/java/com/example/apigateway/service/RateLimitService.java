package com.example.apigateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    // Redis ile reaktif (non-blocking) iletişim kuran template
    // String, String -> anahtar da değer de String tipinde
    private final ReactiveRedisTemplate<String, String> redisTemplate;

    // Bu metod: verilen API key için istek yapılmasına izin var mı?
    // organizationId: hangi organizasyona ait
    // keyId: hangi API key
    // dailyLimit: günlük maksimum istek sayısı
    public Mono<Boolean> isAllowed(UUID organizationId, String keyId, int dailyLimit) {

        // Redis'teki anahtar formatı: "rate_limit:daily:api-key-id"
        // Her API key için ayrı bir sayaç tutuyoruz
        String key = "rate_limit:daily:" + keyId;

        // Redis'ten bu key'in mevcut değerini al
        return redisTemplate.opsForValue().get(key)
                // Eğer Redis'te bu key yoksa (ilk istek) "0" döndür
                .defaultIfEmpty("0")
                .flatMap(currentCount -> {
                    // String'i integer'a çevir
                    int count = Integer.parseInt(currentCount);

                    // Mevcut sayaç günlük limite ulaştıysa isteği reddet
                    if (count >= dailyLimit) {
                        return Mono.just(false); // izin yok
                    }

                    // Limite ulaşılmadıysa sayacı 1 artır
                    return redisTemplate.opsForValue()
                            .increment(key)
                            .flatMap(newCount -> {
                                // newCount == 1 ise bu günün ilk isteği demek
                                if (newCount == 1) {
                                    // 24 saatlik TTL ayarla — yarın gece sayaç otomatik sıfırlanır
                                    return redisTemplate.expire(key, Duration.ofDays(1))
                                            .thenReturn(true); // izin var
                                }
                                // İlk istek değilse direkt izin ver
                                return Mono.just(true);
                            });
                });
    }

    // Bu metod: bu API key için bugün kaç istek hakkı kaldı?
    public Mono<Long> getRemainingRequests(String keyId, int dailyLimit) {
        String key = "rate_limit:daily:" + keyId;

        // Redis'ten mevcut sayacı al
        return redisTemplate.opsForValue().get(key)
                // Yoksa "0" döndür
                .defaultIfEmpty("0")
                // Kalan = toplam limit - kullanılan
                .map(count -> (long) dailyLimit - Long.parseLong(count));
    }
}