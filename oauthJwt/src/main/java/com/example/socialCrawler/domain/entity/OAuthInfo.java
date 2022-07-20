package com.example.socialCrawler.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
public class OAuthInfo {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String oauthId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
