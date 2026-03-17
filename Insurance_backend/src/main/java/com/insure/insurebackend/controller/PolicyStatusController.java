package com.insure.insurebackend.controller;

import com.insure.insurebackend.dto.PolicyResponse;
import com.insure.insurebackend.model.Policy;
import com.insure.insurebackend.service.PolicyService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/policies")
@CrossOrigin(origins = "http://localhost:5173")
public class PolicyStatusController {

    private final PolicyService policyService;
    private final ModelMapper modelMapper;

    public PolicyStatusController(PolicyService policyService, ModelMapper modelMapper) {
        this.policyService = policyService;
        this.modelMapper = modelMapper;
    }

    @PutMapping("/{policyId}/activate")
    public ResponseEntity<PolicyResponse> activatePolicy(@PathVariable Long policyId) {
        Policy policy = policyService.activatePolicy(policyId);
        return ResponseEntity.ok(modelMapper.map(policy, PolicyResponse.class));
    }

    @PutMapping("/{policyId}/deactivate")
    public ResponseEntity<PolicyResponse> deactivatePolicy(@PathVariable Long policyId) {
        Policy policy = policyService.deactivatePolicy(policyId);
        return ResponseEntity.ok(modelMapper.map(policy, PolicyResponse.class));
    }
}
