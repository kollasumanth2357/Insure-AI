package com.insure.insurebackend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AgentAppointmentResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String pincode;
    private String status;
}
