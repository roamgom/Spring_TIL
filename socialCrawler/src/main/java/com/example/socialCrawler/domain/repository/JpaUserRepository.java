package com.example.socialCrawler.domain.repository;

import com.example.socialCrawler.domain.entity.ServiceUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<ServiceUser, Long> {

    Optional<ServiceUser> findByUserName(String username);
}
