package com.insure.insurebackend.controller;

import com.insure.insurebackend.dto.AuthLoginRequest;
import com.insure.insurebackend.dto.AuthLoginResponse;
import com.insure.insurebackend.jwt.JwtUtil;
import com.insure.insurebackend.repository.AgentProfileRepository;
import com.insure.insurebackend.model.User;
import com.insure.insurebackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AgentProfileRepository agentProfileRepository;

    public AuthController(UserService userService,
                          JwtUtil jwtUtil,
                          AgentProfileRepository agentProfileRepository) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.agentProfileRepository = agentProfileRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        Optional<User> user = userService.login(request.getIdentifier(), request.getPassword());

        if (user.isPresent()) {
            if (!user.get().getRole().name().equals(request.getRole())) {
                return ResponseEntity.badRequest()
                        .body(new AuthLoginResponse(null, null, "Invalid Role"));
            }

            if (user.get().getRole().name().equals("AGENT")) {
                boolean inactive = agentProfileRepository.findByUserId(user.get().getId())
                        .map(profile -> "INACTIVE".equalsIgnoreCase(profile.getStatus()))
                        .orElse(false);
                if (inactive) {
                    return ResponseEntity.badRequest()
                            .body(new AuthLoginResponse(null, null, "Agent account is deactivated. Contact admin."));
                }
            }

            String token = jwtUtil.generateToken(
                    user.get().getUsername(),
                    user.get().getRole().name()
            );

            return ResponseEntity.ok(
                    new AuthLoginResponse(token, user.get().getRole().name(), "Login Successful")
            );
        }

        return ResponseEntity.badRequest()
                .body(new AuthLoginResponse(null, null, "Invalid Credentials"));
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<?> checkUsername(@PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(java.util.Map.of("exists", exists));
    }
}
