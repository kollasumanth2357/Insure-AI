package com.insure.insurebackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentConfirmRequest {

    @NotNull
    private Long policyId;

    @NotNull
    private BigDecimal amount;

    @NotBlank
    private String orderId;

    @NotBlank
    private String paymentId;
}
