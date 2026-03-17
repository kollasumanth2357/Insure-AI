package com.insure.insurebackend.controller;

import com.insure.insurebackend.model.ContactMessage;
import com.insure.insurebackend.repository.ContactMessageRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "http://localhost:5173")
public class ContactController {

    private final ContactMessageRepository repository;

    public ContactController(ContactMessageRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> submit(@RequestBody ContactMessage payload) {
        ContactMessage message = new ContactMessage();
        message.setName(payload.getName());
        message.setEmail(payload.getEmail());
        message.setSubject(payload.getSubject());
        message.setMessage(payload.getMessage());

        repository.save(message);

        return ResponseEntity.ok(
                Map.of("message", "Thank you! Your message has been received.")
        );
    }
}

