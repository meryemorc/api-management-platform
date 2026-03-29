package com.example.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "email boş olamaz")
    @Email(message = "geçerli bir email girin")
    private String email;

    @NotBlank(message = "şifre bos olamaz")
    @Size(min = 8 , message = "sifre en az 8 karakter olmalı")
    private String password;

}
