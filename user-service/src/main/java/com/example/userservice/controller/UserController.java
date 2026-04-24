package com.example.userservice.controller;

import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/by-email/{email}")
    public ResponseEntity<Map<String, String>> getUserByEmail(@PathVariable String email) {
        return userRepository.findByEmail(email)
                .map(user -> ResponseEntity.ok(Map.of(
                        "userId", user.getId().toString(),
                        "email", user.getEmail()
                )))
                .orElse(ResponseEntity.notFound().build());
    }
}