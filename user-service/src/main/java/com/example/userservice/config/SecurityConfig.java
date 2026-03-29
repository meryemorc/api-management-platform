package com.example.userservice.config;

import com.example.userservice.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // spring security aktif
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll() //login ve register izinleri acık
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated() // geri kalan butun isteklere token zorunlu
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)//sessıon tutumuyoz her ıstek tokenıyla geliyo
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        //spring securıtyn kendı filterından önce calıstır

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() { //spring securitye kullanıcıyı nasıl dogrulayacagını anlatan yapı
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); //Dao veritabanından kullanıcı cekip dogrulayan provider
        provider.setUserDetailsService(userDetailsService); //kullanıcıyı hangı servisle cekip dogrulayacagını söylüyor
        provider.setPasswordEncoder(passwordEncoder()); //sifreyi su encoder ile ççzöümle bcrypt karsılastırıyor bcrypt tek yönlü calısır hashlenmis sifre tekrar acvılamz
        // sadece düz sifre bu hashe karsılık geliyor mu diye kontrol edebiliriz
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        //sifre hashleniyo
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
        //loginde sifre email dogrulaması icin
    }
}
