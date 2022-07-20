package com.example.socialCrawler.controller;

import com.example.socialCrawler.domain.dto.UserSelfDto;
import com.example.socialCrawler.domain.entity.User;
import com.example.socialCrawler.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@PreAuthorize("hasRole('ROLE_USER')")
public class UserController {
    @GetMapping("/me")
    public UserSelfDto retrieveUserSelfById() {
        UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return UserSelfDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
