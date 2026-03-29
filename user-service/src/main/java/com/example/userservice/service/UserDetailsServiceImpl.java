package com.example.userservice.service;

import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    //bu metoda spring security login sırasında cagırıp emaili veriyor optiona<user> dönüyor
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(UserPrincipal::new)
                //user nesnesini al userprincipala cevir
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + email));
                // yoksa exception fırlat
    }
}

