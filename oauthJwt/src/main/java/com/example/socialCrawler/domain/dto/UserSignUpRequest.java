package com.example.socialCrawler.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserSignUpRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
