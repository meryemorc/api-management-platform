package com.example.userservice.service;

import com.example.userservice.dto.request.LoginRequest;
import com.example.userservice.dto.request.RegisterRequest;
import com.example.userservice.dto.response.AuthResponse;
import com.example.userservice.enums.AuthProvider;
import com.example.userservice.repository.RoleRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.UserPrincipal;
import com.example.userservice.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.example.userservice.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.userservice.exception.DuplicateResourceException;
import com.example.userservice.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder; //sifre hashleme
    private final JwtService jwtService; //token üretme
    private final AuthenticationManager authenticationManager; //login dogrulama

    public AuthResponse register(RegisterRequest request) { //register isteginden nesne üretip bu nesnelerin fieldlarıyla user üretiyo
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Bu email zaten kayıtlı");
        }

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .isEmailVerified(false) // email henüz dogrulanmadı
                .authProvider(AuthProvider.LOCAL)//google veya github kaydı değil local kayıt
                .createdAt(LocalDateTime.now())
                .roles(List.of(roleRepository.findByName("ORG_MEMBER")
                        .orElseThrow(() -> new ResourceNotFoundException("Rol bulunamadı"))))
                .build(); //user build ediyor

        userRepository.save(user);

        var userPrincipal = new UserPrincipal(user);// userı user principalla spring securitynın tanıyacagı formata getıryoruz
        var accessToken = jwtService.generateToken(userPrincipal, userPrincipal.getUserId());//usera token uretıyoruz kullanıcı tekrar token üretmek zorunda kalmıyor
        var refreshToken = jwtService.generateRefreshToken(userPrincipal); //user token güncelliyoruz

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build(); //build objeyi oluşturuyor ve döndürüyor
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        var userPrincipal = new UserPrincipal(user);
        var accessToken = jwtService.generateToken(userPrincipal, userPrincipal.getUserId());
        var refreshToken = jwtService.generateRefreshToken(userPrincipal);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }
}