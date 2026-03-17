package com.insure.insurebackend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PolicyApplyStartResponse {

    private Long policyId;
    private String policyName;
    private String policyCategory;
    private BigDecimal premiumAmount;
    private BigDecimal coverageAmount;
    private String billingCycle;
    private String customerName;
    private String customerEmail;
    private String phone;
    private String doorNo;
    private String buildingName;
    private String street;
    private String area;
    private String city;
    private String district;
    private String state;
    private String pincode;
}
