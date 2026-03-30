package com.example.userservice.controller;

import com.example.userservice.dto.request.LoginRequest;
import com.example.userservice.dto.request.RegisterRequest;
import com.example.userservice.dto.response.AuthResponse;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController//Httap isteklerini karsılıyor
@RequestMapping("/api/v1/auth") //tüm endpointlerin base url'i
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register") //response entity http cevabının tamamını tutuyor hem body hem status kodu
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        //Auth response ise ne dönecegini gösteriyor burda
        //valid anotasyonu registerrequest dtosu icerisne koydugumuz anotasyonları kullanılabilir hale getiriyor
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
}
