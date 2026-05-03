package com.example.userservice.controller;

import com.example.userservice.dto.response.UserResponse;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.jwt.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "Kullanıcı işlemleri")
public class UserController {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    /**
     * Servisler arası internal endpoint.
     * Organization Service bu endpoint'i email → userId dönüşümü için çağırıyor.
     * SecurityConfig'te permitAll yapılmış, dışarıya kapalı tutulmalı (Gateway seviyesinde).
     */
    @Operation(summary = "Email ile kullanıcı bul", description = "Servisler arası internal kullanım içindir")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Kullanıcı bulundu"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı")
    })
    @GetMapping("/by-email/{email}")
    public ResponseEntity<Map<String, String>> getUserByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email)
                .map(user -> ResponseEntity.ok(Map.of(
                        "userId", user.getId().toString(),
                        "email", user.getEmail(),
                        "username", user.getUsername()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * JWT token'dan mevcut kullanıcı bilgilerini döndürür.
     * Gateway bu endpoint'e X-User-Email header'ı ekleyerek yönlendirir.
     * Token'ı Gateway doğruladığı için burada tekrar doğrulamaya gerek yok.
     */
    @Operation(summary = "Mevcut kullanıcı bilgileri", description = "JWT token'dan kullanıcı bilgilerini döndürür")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Kullanıcı bilgileri"),
            @ApiResponse(responseCode = "404", description = "Kullanıcı bulunamadı")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @RequestHeader(value = "X-User-Email", required = false) String emailFromGateway,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        // Gateway üzerinden geliyorsa X-User-Email header'ı zaten set edilmiş olur
        // Direkt erişimde (development/test) Authorization header'ından parse ediyoruz
        String email = emailFromGateway != null
                ? emailFromGateway
                : jwtService.extractEmail(authHeader.substring(7));

        return userRepository.findByEmail(email)
                .map(user -> {
                    List<String> roleNames = user.getRoles().stream()
                            .map(role -> role.getName())
                            .collect(Collectors.toList());

                    return ResponseEntity.ok(UserResponse.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .isActive(user.isActive())
                            .isEmailVerified(user.isEmailVerified())
                            .roles(roleNames)
                            .build());
                })
                .orElse(ResponseEntity.notFound().build());
    }
}