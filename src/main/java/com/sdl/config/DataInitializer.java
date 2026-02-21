package com.sdl.config;

import com.sdl.entity.User;
import com.sdl.entity.User.Role;
import com.sdl.entity.User.UserStatus;
import com.sdl.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = User.builder()
                        .email("admin@example.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .status(UserStatus.APPROVED)
                        .active(true)
                        .build();
                userRepository.save(admin);
                System.out.println("Root Admin created: admin@example.com / admin123");
            }
        };
    }
}
