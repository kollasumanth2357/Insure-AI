package com.insure.insurebackend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long id;
    private Long policyId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime paidAt;
}
