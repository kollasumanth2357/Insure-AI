package com.insure.insurebackend.dto;

import lombok.Data;

@Data
public class AgentUpdateRequest {
    private String fullName;
    private String phone;
    private Integer experienceYears;
    private String specialization;
    private String serviceAreas;
    private String availabilityStatus;
    private String pincode;
    private Double latitude;
    private Double longitude;
    private Boolean active;
    private AddressDto address;
}
