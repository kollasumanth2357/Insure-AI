package com.insure.insurebackend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PolicyPaymentResponse {

    private String message;
    private String receiptNumber;
    private Long customerPolicyId;
    private Long policyId;
    private String policyName;
    private BigDecimal amount;
    private String paymentMethod;
    private LocalDateTime paidAt;
    private List<String> selectedDocuments;
}
