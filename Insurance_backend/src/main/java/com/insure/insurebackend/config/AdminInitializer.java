package com.insure.insurebackend.config;

import com.insure.insurebackend.model.Role;
import com.insure.insurebackend.model.User;
import com.insure.insurebackend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner createAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            boolean adminExists = userRepository.countByRole(Role.ADMIN) > 0;

            if (!adminExists) {
                User admin = new User();
                admin.setFullName("KOLLA SUMANTH");
                admin.setUsername("sumanth");
                admin.setEmail("kollasumanth2357@gmail.com");
                admin.setPhone("7013575636");
                admin.setPassword(passwordEncoder.encode("Sumanth@123"));
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);

                System.out.println("====================================");
                System.out.println("DEFAULT ADMIN ACCOUNT CREATED");
                System.out.println("Username: sumanth");
                System.out.println("Password: Sumanth@123");
                System.out.println("====================================");
            }
        };
    }
}
