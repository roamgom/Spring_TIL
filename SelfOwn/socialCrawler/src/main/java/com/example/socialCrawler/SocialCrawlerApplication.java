package com.example.socialCrawler;

import com.example.socialCrawler.domain.entity.ServiceUser;
import com.example.socialCrawler.domain.repository.JpaUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class SocialCrawlerApplication {

	private final JpaUserRepository repository;

	private final PasswordEncoder passwordEncoder;

	public SocialCrawlerApplication(JpaUserRepository repository, PasswordEncoder passwordEncoder) {
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
	}

	@PostConstruct
	public void initUsers() {
		ServiceUser user = ServiceUser.builder()
				.userName("admin")
				.password(passwordEncoder.encode("test1234"))
				.build();

		repository.save(user);
	}

	public static void main(String[] args) {
		SpringApplication.run(SocialCrawlerApplication.class, args);
	}

}
