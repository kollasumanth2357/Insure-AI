package com.insure.insurebackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PolicyRequest {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private BigDecimal premiumAmount;
    private String mainCategory;
    private String subCategory;
    private BigDecimal coverageAmount;
    private String billingCycle;
    private Boolean active;
}
