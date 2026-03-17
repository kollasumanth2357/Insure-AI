package com.insure.insurebackend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CustomerAppointmentResponse {
    private Long id;
    private Long agentId;
    private String agentName;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String pincode;
    private String status;
}
