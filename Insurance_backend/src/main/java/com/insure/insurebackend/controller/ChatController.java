package com.insure.insurebackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    public record ChatRequest(String message) {}

    @PostMapping
    public ResponseEntity<Map<String, String>> chat(@RequestBody ChatRequest request) {
        String question = request.message() == null ? "" : request.message().toLowerCase();

        String reply;
        if (question.contains("login") || question.contains("sign in")) {
            reply = "To login, use the Login page. After logging in, you will be redirected to the correct dashboard based on your role (ADMIN, AGENT or CUSTOMER).";
        } else if (question.contains("register") || question.contains("sign up")) {
            reply = "Use the Register page to create a new account. Fill in all required fields carefully; once registered, you can login and access your dashboard.";
        } else if (question.contains("profile")) {
            reply = "You can manage your personal details and upload documents from the Profile page. Open the profile menu in the top navigation and click Profile.";
        } else if (question.contains("dashboard") || question.contains("admin") || question.contains("agent") || question.contains("customer")) {
            reply = "Each role has its own dashboard. After login you will see ADMIN, AGENT or CUSTOMER dashboard features depending on the role stored for your account.";
        } else if (question.contains("form") || question.contains("fill")) {
            reply = "When filling forms, complete all mandatory fields marked as required. If any field is unclear, describe it here and I can guide what it typically expects.";
        } else {
            reply = "I can help with questions about logging in, registration, dashboards, profile, and how to fill forms in this insurance application. Please ask in a short sentence about the area you need help with.";
        }

        Map<String, String> body = new HashMap<>();
        body.put("reply", reply);
        return ResponseEntity.ok(body);
    }
}

