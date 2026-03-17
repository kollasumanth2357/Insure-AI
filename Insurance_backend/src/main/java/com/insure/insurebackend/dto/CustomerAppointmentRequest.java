package com.insure.insurebackend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CustomerAppointmentRequest {
    private Long customerId;
    private Long agentId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String pincode;
    private String notes;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private Double customerLatitude;
    private Double customerLongitude;
}
