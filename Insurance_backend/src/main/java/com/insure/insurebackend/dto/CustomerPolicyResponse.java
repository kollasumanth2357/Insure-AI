package com.insure.insurebackend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerPolicyResponse {
    private Long id;
    private Long policyId;
    private String policyName;
    private String policyType;
    private String status;
    private LocalDateTime purchaseDate;
    private Long agentId;
}
