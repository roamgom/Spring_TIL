package com.example.socialCrawler.domain.repository;

import com.example.socialCrawler.domain.entity.ServiceUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<ServiceUser, Long> {

    ServiceUser findByUserName(String username);
}
