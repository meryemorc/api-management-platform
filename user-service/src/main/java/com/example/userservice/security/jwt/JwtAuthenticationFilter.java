package com.example.userservice.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    //Her http istegini icin OncePerRequestFilter classını bir kez clıstırır

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    //userserviceDetail spring bootun kendi interface'i

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        //beareri atıyor tokendan
        final String email = jwtService.extractEmail(jwt);
        //tokendan sub kısmı cıkarıyor generatetokenda sub kısma emaili tanımlamıstık yani tokenın kimin oldugunu gösteriyor


        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                                //su an kim login bilgisini tutuyor
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            //emaile göre kullanıcı cekme

            if (jwtService.isTokenValid(jwt, userDetails)) {

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                        //springin authentication objesi kullanıcı bilgilerini taısyor passwordu tokendan aldık diye null
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                //tokendan kullanıcıyı spring securitye tanıtıyoruz bu istek su kullanıcıdan geldi bilgisi oluyor
            }
        }

        filterChain.doFilter(request, response);
        //filtrelemeye devam et demek
    }
}