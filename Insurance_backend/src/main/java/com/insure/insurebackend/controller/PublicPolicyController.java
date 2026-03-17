package com.insure.insurebackend.controller;

import com.insure.insurebackend.service.PolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/policies")
@CrossOrigin(origins = "http://localhost:5173")
public class PublicPolicyController {

    private final PolicyService policyService;

    public PublicPolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PutMapping("/{id}/delete")
    public ResponseEntity<?> deletePolicy(@PathVariable Long id) {
        policyService.deletePolicy(id);
        return ResponseEntity.ok(java.util.Map.of("message", "Policy deleted"));
    }
}
