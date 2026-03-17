package com.insure.insurebackend.service;

import com.insure.insurebackend.dto.PolicyRequest;
import com.insure.insurebackend.model.Policy;
import com.insure.insurebackend.repository.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PolicyService {

    private final PolicyRepository policyRepository;
    public PolicyService(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    public List<Policy> getActivePolicies() {
        return policyRepository.findActivePolicies();
    }

    public List<Policy> getAllPolicies() {
        return policyRepository.findAllPolicies();
    }

    @Transactional
    public Policy createPolicy(PolicyRequest request) {
        Policy policy = new Policy();
        policy.setName(request.getName());
        policy.setDescription(request.getDescription());
        policy.setPremiumAmount(request.getPremiumAmount());
        policy.setMainCategory(request.getMainCategory());
        policy.setSubCategory(request.getSubCategory());
        policy.setCoverageAmount(request.getCoverageAmount());
        policy.setBillingCycle(request.getBillingCycle());
        boolean isActive = request.getActive() == null || request.getActive();
        policy.setActive(isActive);
        policy.setStatus(isActive ? "ACTIVE" : "INACTIVE");
        policy.setIsDeleted(false);
        return policyRepository.save(policy);
    }

    @Transactional
    public Policy updatePolicy(Long id, PolicyRequest request) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Policy not found"));

        policy.setName(request.getName());
        policy.setDescription(request.getDescription());
        policy.setPremiumAmount(request.getPremiumAmount());
        if (request.getMainCategory() != null) {
            policy.setMainCategory(request.getMainCategory());
        }
        if (request.getSubCategory() != null) {
            policy.setSubCategory(request.getSubCategory());
        }
        if (request.getCoverageAmount() != null) {
            policy.setCoverageAmount(request.getCoverageAmount());
        }
        if (request.getBillingCycle() != null) {
            policy.setBillingCycle(request.getBillingCycle());
        }
        if (request.getActive() != null) {
            policy.setActive(request.getActive());
            policy.setStatus(Boolean.TRUE.equals(request.getActive()) ? "ACTIVE" : "INACTIVE");
        }
        return policyRepository.save(policy);
    }

    @Transactional
    public Policy deletePolicy(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Policy not found"));
        policy.setActive(false);
        policy.setStatus("INACTIVE");
        policy.setIsDeleted(true);
        return policyRepository.save(policy);
    }

    @Transactional
    public Policy activatePolicy(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Policy not found"));
        policy.setActive(true);
        policy.setStatus("ACTIVE");
        policy.setIsDeleted(false);
        return policyRepository.save(policy);
    }

    @Transactional
    public Policy deactivatePolicy(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Policy not found"));
        policy.setActive(false);
        policy.setStatus("INACTIVE");
        policy.setIsDeleted(false);
        return policyRepository.save(policy);
    }
}
