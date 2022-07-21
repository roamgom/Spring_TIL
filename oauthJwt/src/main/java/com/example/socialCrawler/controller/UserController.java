package com.example.socialCrawler.controller;

import com.example.socialCrawler.domain.dto.UserDto;
import com.example.socialCrawler.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@PreAuthorize("hasRole('ROLE_USER')")
public class UserController {
    // 유저 Controller

    @GetMapping("/me")
    public UserDto retrieveUserSelfByAuthPrincipal() {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return UserDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
