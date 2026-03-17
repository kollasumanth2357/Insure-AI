package com.insure.insurebackend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PolicyResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal premiumAmount;
    private String mainCategory;
    private String subCategory;
    private BigDecimal coverageAmount;
    private String billingCycle;
    private Boolean active;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
