package com.insure.insurebackend.service;

import com.insure.insurebackend.dto.PolicyDocumentRequirementResponse;
import com.insure.insurebackend.model.Policy;
import com.insure.insurebackend.repository.PolicyRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PolicyDocumentRequirementService {

    private final PolicyRepository policyRepository;

    public PolicyDocumentRequirementService(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    public List<PolicyDocumentRequirementResponse> getDocuments(Long policyId) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("Policy not found"));
        String category = policy.getMainCategory() == null ? "" : policy.getMainCategory().toUpperCase();
        List<PolicyDocumentRequirementResponse> documents = new ArrayList<>();
        documents.add(new PolicyDocumentRequirementResponse("Aadhaar Card", "ID_PROOF"));
        documents.add(new PolicyDocumentRequirementResponse("Address Proof", "ADDRESS_PROOF"));

        if (category.contains("VEHICLE") || category.contains("CAR") || category.contains("BIKE")) {
            documents.add(new PolicyDocumentRequirementResponse("Driving License", "LICENSE"));
            documents.add(new PolicyDocumentRequirementResponse("Vehicle RC", "VEHICLE_RC"));
        } else if (category.contains("HEALTH")) {
            documents.add(new PolicyDocumentRequirementResponse("Medical Report", "MEDICAL"));
        } else if (category.contains("LIFE")) {
            documents.add(new PolicyDocumentRequirementResponse("Income Proof", "INCOME"));
        } else if (category.contains("HOME")) {
            documents.add(new PolicyDocumentRequirementResponse("Property Proof", "PROPERTY"));
        }
        return documents;
    }
}
