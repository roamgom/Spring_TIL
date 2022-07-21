package com.example.socialCrawler.controller;

import com.example.socialCrawler.domain.dto.AuthRequest;
import com.example.socialCrawler.domain.dto.AuthResponse;
import com.example.socialCrawler.domain.dto.UserDto;
import com.example.socialCrawler.domain.dto.UserSignUpRequest;
import com.example.socialCrawler.security.UserPrincipal;
import com.example.socialCrawler.security.jwt.TokenProvider;
import com.example.socialCrawler.service.CustomUserDetailsService;
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
    // 인증 Controller
    // JWT 토큰인증 기반
    // 로그인, 토큰 유효성 확인
    // (회원가입은 OAuth2로 진행가능하기에 선택적으로 구현)

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    private final CustomUserDetailsService userDetailsService;

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

    @PostMapping("/signup")
    public UserDto signupUser(@Valid @RequestBody UserSignUpRequest request) {
        UserPrincipal user = (UserPrincipal) userDetailsService.registerNewUser(request);
        return UserDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    @GetMapping("/token")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        boolean validateToken = tokenProvider.validateToken(token);
        if (validateToken) {
            return ResponseEntity.ok(new AuthResponse(token));
        } else {
            throw new IllegalArgumentException("Invalid Token");
        }
    }
}
