package com.insure.insurebackend.controller;

import com.insure.insurebackend.dto.AppointmentRequest;
import com.insure.insurebackend.dto.AppointmentResponse;
import com.insure.insurebackend.model.Appointment;
import com.insure.insurebackend.model.User;
import com.insure.insurebackend.repository.AgentProfileRepository;
import com.insure.insurebackend.service.AppointmentService;
import com.insure.insurebackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins = "http://localhost:5173")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserService userService;
    private final AgentProfileRepository agentProfileRepository;

    public AppointmentController(AppointmentService appointmentService,
                                 UserService userService,
                                 AgentProfileRepository agentProfileRepository) {
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.agentProfileRepository = agentProfileRepository;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(@Valid @RequestBody AppointmentRequest request,
                                                                 Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("Unauthorized");
        }
        User customer = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (request.getCustomerId() != null && !request.getCustomerId().equals(customer.getId())) {
            throw new IllegalArgumentException("Unauthorized appointment request");
        }
        Appointment appointment = appointmentService.bookAppointment(customer.getId(), request);
        return ResponseEntity.ok(toAppointmentResponse(appointment));
    }

    private AppointmentResponse toAppointmentResponse(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setCustomerId(appointment.getCustomer().getId());
        response.setCustomerName(resolveCustomerField(appointment.getCustomerName(), appointment.getCustomer().getFullName()));
        response.setCustomerEmail(resolveCustomerField(appointment.getCustomerEmail(), appointment.getCustomer().getEmail()));
        response.setCustomerPhone(resolveCustomerField(appointment.getCustomerPhone(), appointment.getCustomer().getPhone()));
        response.setAgentId(appointment.getAgent().getId());
        response.setAgentName(appointment.getAgent().getFullName());
        response.setAgentSpecialization(agentProfileRepository.findByUserId(appointment.getAgent().getId())
                .map(profile -> formatSpecialization(profile.getSpecialization().name()))
                .orElse("UNSPECIFIED"));
        response.setAppointmentTime(appointment.getAppointmentTime());
        response.setPincode(appointment.getPincode());
        response.setStatus(normalizeStatus(appointment.getStatus().name()));
        response.setNotes(appointment.getNotes());
        response.setCustomerLatitude(appointment.getCustomerLatitude());
        response.setCustomerLongitude(appointment.getCustomerLongitude());
        return response;
    }

    private String normalizeStatus(String status) {
        return "SCHEDULED".equalsIgnoreCase(status) ? "PENDING" : status;
    }

    private String formatSpecialization(String specialization) {
        return specialization.replace("_", " ");
    }

    private String resolveCustomerField(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
