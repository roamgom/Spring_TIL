package com.example.socialCrawler.repository;

import com.example.socialCrawler.domain.entity.AuthProvider;
import com.example.socialCrawler.domain.entity.OAuthInfo;
import com.example.socialCrawler.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import javax.validation.constraints.NotNull;
import java.util.Optional;

public interface JpaOAuthInfoRepository extends JpaRepository<OAuthInfo, Long> {

    Optional<OAuthInfo> findByEmail(String email);

    Optional<OAuthInfo> findByEmailAndAuthProvider(String email, @NotNull AuthProvider authProvider);
}
