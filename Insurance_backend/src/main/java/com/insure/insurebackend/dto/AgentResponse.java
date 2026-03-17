package com.insure.insurebackend.dto;

import lombok.Data;

@Data
public class AgentResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String username;
    private String email;
    private String phone;
    private Integer experienceYears;
    private String specialization;
    private String serviceAreas;
    private String availabilityStatus;
    private String status;
    private String pincode;
    private Double latitude;
    private Double longitude;
    private Double revenueGenerated;
    private Integer totalAppointments;
    private Boolean softDeleted;
}
