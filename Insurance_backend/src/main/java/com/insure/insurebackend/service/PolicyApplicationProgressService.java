package com.insure.insurebackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insure.insurebackend.dto.PolicyResumeResponse;
import com.insure.insurebackend.dto.PolicySaveProgressRequest;
import com.insure.insurebackend.model.Policy;
import com.insure.insurebackend.model.PolicyApplicationProgress;
import com.insure.insurebackend.model.User;
import com.insure.insurebackend.repository.PolicyApplicationProgressRepository;
import com.insure.insurebackend.repository.PolicyRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class PolicyApplicationProgressService {

    private final PolicyApplicationProgressRepository progressRepository;
    private final PolicyRepository policyRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public PolicyApplicationProgressService(PolicyApplicationProgressRepository progressRepository,
                                            PolicyRepository policyRepository,
                                            UserService userService) {
        this.progressRepository = progressRepository;
        this.policyRepository = policyRepository;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    public void saveProgress(String username, PolicySaveProgressRequest request) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Policy policy = policyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new IllegalArgumentException("Policy not found"));
        PolicyApplicationProgress progress = progressRepository
                .findTopByUserIdAndPolicyIdOrderByUpdatedAtDesc(user.getId(), policy.getId())
                .orElseGet(PolicyApplicationProgress::new);
        progress.setUser(user);
        progress.setPolicy(policy);
        progress.setStepNumber(request.getStep() == null ? 1 : request.getStep());
        progress.setAddressJson(writeJson(request.getAddress()));
        progress.setUploadedDocumentsJson(writeJson(request.getUploadedDocuments()));
        progress.setOtpVerified(Boolean.TRUE.equals(request.getOtpVerified()));
        progress.setPaymentStatus(request.getPaymentStatus());
        progress.setCompleted("SUCCESS".equalsIgnoreCase(request.getPaymentStatus()));
        progressRepository.save(progress);
    }

    public PolicyResumeResponse resume(Long userId, Long policyId) {
        PolicyApplicationProgress progress = (policyId == null
                ? progressRepository.findTopByUserIdOrderByUpdatedAtDesc(userId)
                : progressRepository.findTopByUserIdAndPolicyIdOrderByUpdatedAtDesc(userId, policyId))
                .orElse(null);
        if (progress == null) {
            return null;
        }

        PolicyResumeResponse response = new PolicyResumeResponse();
        response.setPolicyId(progress.getPolicy().getId());
        response.setStep(progress.getStepNumber());
        response.setAddress(readMap(progress.getAddressJson()));
        response.setUploadedDocuments(readList(progress.getUploadedDocumentsJson()));
        response.setOtpVerified(progress.getOtpVerified());
        response.setPaymentStatus(progress.getPaymentStatus());
        response.setCompleted(progress.getCompleted());
        return response;
    }

    private String writeJson(Object value) {
        try {
            return value == null ? null : objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unable to save progress");
        }
    }

    private Map<String, Object> readMap(String value) {
        try {
            return value == null ? Collections.emptyMap() : objectMapper.readValue(value, new TypeReference<>() {});
        } catch (Exception ex) {
            return Collections.emptyMap();
        }
    }

    private List<Map<String, Object>> readList(String value) {
        try {
            return value == null ? Collections.emptyList() : objectMapper.readValue(value, new TypeReference<>() {});
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
