package com.example.socialCrawler.service;

import com.example.socialCrawler.domain.dto.UserSignUpRequest;
import com.example.socialCrawler.domain.entity.User;
import com.example.socialCrawler.repository.UserRepository;
import com.example.socialCrawler.exception.ResourceNotFoundException;
import com.example.socialCrawler.exception.UserAlreadyExistsException;
import com.example.socialCrawler.security.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User name not found: " + username));

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails registerNewUser(UserSignUpRequest joinRequest) throws UserAlreadyExistsException {
        Optional<User> findUser = userRepository.findByEmail(joinRequest.getEmail());
        if (findUser.isPresent()) {
            throw new UserAlreadyExistsException("User with email already exists");
        } else {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            User newUser = User.builder()
                    .name(joinRequest.getName())
                    .email(joinRequest.getEmail())
                    .password(passwordEncoder.encode(joinRequest.getPassword()))
                    .build();
            User user = userRepository.save(newUser);
            return UserPrincipal.create(user);
        }
    }
}
