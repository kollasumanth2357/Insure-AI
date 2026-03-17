package com.insure.insurebackend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private Long agentId;
    private String agentName;
    private String agentSpecialization;
    private Long policyId;
    private String policyName;
    private String policyType;
    private LocalDateTime appointmentTime;
    private String pincode;
    private String status;
    private String notes;
    private Double customerLatitude;
    private Double customerLongitude;
}
