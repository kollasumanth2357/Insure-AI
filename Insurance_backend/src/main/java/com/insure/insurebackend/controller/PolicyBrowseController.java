package com.insure.insurebackend.controller;

import com.insure.insurebackend.dto.PolicyResponse;
import com.insure.insurebackend.model.Policy;
import com.insure.insurebackend.service.PolicyService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/policies")
@CrossOrigin(origins = "http://localhost:5173")
public class PolicyBrowseController {

    private final PolicyService policyService;
    private final ModelMapper modelMapper;

    public PolicyBrowseController(PolicyService policyService, ModelMapper modelMapper) {
        this.policyService = policyService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<PolicyResponse>> listActivePolicies() {
        List<PolicyResponse> response = policyService.getActivePolicies()
                .stream()
                .map(policy -> modelMapper.map(policy, PolicyResponse.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
