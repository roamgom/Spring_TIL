package com.example.socialCrawler.controller;

import com.example.socialCrawler.domain.dto.AuthRequest;
import com.example.socialCrawler.domain.dto.AuthResponse;
import com.example.socialCrawler.domain.repository.UserRepository;
import com.example.socialCrawler.exception.OAuth2AuthenticationProcessingException;
import com.example.socialCrawler.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(),
                        authRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @GetMapping("hello")
    public String test() {
        return "Hello";
    }

    @GetMapping("/login/oauth")
    public ResponseEntity<?> oauthLogin(@RequestParam String token) {
        boolean validateToken = tokenProvider.validateToken(token);
        if (validateToken) {
            return ResponseEntity.ok(new AuthResponse(token));
        } else {
            throw new OAuth2AuthenticationProcessingException("Invalid Token");
        }
    }
}
