package com.insure.insurebackend.service;

import com.insure.insurebackend.model.Role;
import com.insure.insurebackend.model.User;
import com.insure.insurebackend.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AgentProfileProvisioningService agentProfileProvisioningService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AgentProfileProvisioningService agentProfileProvisioningService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.agentProfileProvisioningService = agentProfileProvisioningService;
    }

    // ================= REGISTER =================
    public User register(User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setProfileImage(null);

        return userRepository.save(user);
    }

    // ================= LOGIN (USERNAME OR EMAIL) =================
    public Optional<User> login(String identifier, String password) {

        Optional<User> user =
                userRepository.findByUsernameOrEmail(identifier, identifier);

        if (user.isPresent()
                && passwordEncoder.matches(password, user.get().getPassword())) {
            return user;
        }

        return Optional.empty();
    }

    // ================= USERNAME CHECK =================
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // ================= FIND USER =================
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // ================= SAVE USER =================
    public User save(User user) {
        User savedUser = userRepository.save(user);
        agentProfileProvisioningService.ensureDefaultProfile(savedUser);
        return savedUser;
    }

    // ================= CHANGE PASSWORD =================
    public void changePassword(User user,
                               String currentPassword,
                               String newPassword) {

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
