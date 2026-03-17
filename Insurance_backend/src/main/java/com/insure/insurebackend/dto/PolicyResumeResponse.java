package com.insure.insurebackend.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PolicyResumeResponse {

    private Long policyId;
    private Integer step;
    private Map<String, Object> address;
    private List<Map<String, Object>> uploadedDocuments;
    private Boolean otpVerified;
    private String paymentStatus;
    private Boolean completed;
}
