package com.insure.insurebackend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentRequest {
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private Double customerLatitude;
    private Double customerLongitude;
    private Long agentId;
    private Long policyId;
    private LocalDateTime appointmentTime;
    private LocalDateTime appointmentDate;
    private String pincode;
    private String notes;
}
