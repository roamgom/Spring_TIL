package com.example.socialCrawler;

import com.example.socialCrawler.config.AppProperties;
import com.example.socialCrawler.domain.entity.User;
import com.example.socialCrawler.domain.entity.UserRole;
import com.example.socialCrawler.domain.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class SocialCrawlerApplication {

	private final UserRepository repository;

	private final PasswordEncoder passwordEncoder;

	public SocialCrawlerApplication(UserRepository repository, PasswordEncoder passwordEncoder) {
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
	}

	@PostConstruct
	public void initUsers() {
		User user = User.builder()
				.name("admin")
				.email("admin@social.com")
				.password(passwordEncoder.encode("test1234"))
				.userRole(UserRole.ADMIN)
				.build();

		repository.save(user);
	}

	public static void main(String[] args) {
		SpringApplication.run(SocialCrawlerApplication.class, args);
	}

}
