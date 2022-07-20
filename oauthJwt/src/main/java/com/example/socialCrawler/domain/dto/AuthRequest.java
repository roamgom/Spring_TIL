package com.example.socialCrawler.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class AuthRequest {

    @NotBlank
//    @Email
    private String email;
    @NotBlank
    private String password;
}
