package com.insure.insurebackend.dto;

import lombok.Data;

@Data
public class NearbyAgentResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String specialization;
    private String pincode;
    private Double latitude;
    private Double longitude;
}
