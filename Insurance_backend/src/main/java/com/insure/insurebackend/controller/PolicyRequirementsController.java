package com.insure.insurebackend.controller;

import com.insure.insurebackend.dto.PolicyDocumentRequirementResponse;
import com.insure.insurebackend.dto.PolicyResumeResponse;
import com.insure.insurebackend.dto.PolicySaveProgressRequest;
import com.insure.insurebackend.service.PolicyApplicationProgressService;
import com.insure.insurebackend.service.PolicyDocumentRequirementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class PolicyRequirementsController {

    private final PolicyDocumentRequirementService requirementService;
    private final PolicyApplicationProgressService progressService;

    public PolicyRequirementsController(PolicyDocumentRequirementService requirementService,
                                        PolicyApplicationProgressService progressService) {
        this.requirementService = requirementService;
        this.progressService = progressService;
    }

    @GetMapping("/api/policies/{policyId}/documents")
    public ResponseEntity<List<PolicyDocumentRequirementResponse>> documents(@PathVariable Long policyId) {
        return ResponseEntity.ok(requirementService.getDocuments(policyId));
    }

    @PostMapping("/api/policy/save-progress")
    public ResponseEntity<?> saveProgress(@RequestBody PolicySaveProgressRequest request,
                                          Authentication authentication) {
        progressService.saveProgress(authentication.getName(), request);
        return ResponseEntity.ok(java.util.Map.of("message", "Progress saved"));
    }

    @GetMapping("/api/policy/resume/{userId}")
    public ResponseEntity<PolicyResumeResponse> resume(@PathVariable Long userId,
                                                       @RequestParam(required = false) Long policyId) {
        return ResponseEntity.ok(progressService.resume(userId, policyId));
    }
}
