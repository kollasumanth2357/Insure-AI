package com.insure.insurebackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerPolicyRequest {
    @NotNull
    private Long policyId;
    @NotNull
    private BigDecimal amount;
}
