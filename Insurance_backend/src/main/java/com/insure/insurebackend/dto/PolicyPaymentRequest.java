package com.insure.insurebackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PolicyPaymentRequest {

    @NotNull
    private Long policyId;

    @NotNull
    private BigDecimal amount;

    @NotBlank
    private String paymentMethod;

    @NotEmpty
    private List<String> selectedDocuments;
}
