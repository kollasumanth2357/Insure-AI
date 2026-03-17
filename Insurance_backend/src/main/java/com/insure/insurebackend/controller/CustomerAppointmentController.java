package com.insure.insurebackend.controller;

import com.insure.insurebackend.dto.CustomerAppointmentRequest;
import com.insure.insurebackend.dto.CustomerAppointmentResponse;
import com.insure.insurebackend.model.Appointment;
import com.insure.insurebackend.model.User;
import com.insure.insurebackend.service.AppointmentService;
import com.insure.insurebackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerAppointmentController {

    private final AppointmentService appointmentService;
    private final UserService userService;

    public CustomerAppointmentController(AppointmentService appointmentService,
                                         UserService userService) {
        this.appointmentService = appointmentService;
        this.userService = userService;
    }

    @PostMapping("/appointments")
    public ResponseEntity<CustomerAppointmentResponse> bookAppointment(@Valid @RequestBody CustomerAppointmentRequest request,
                                                                       Principal principal) {
        Long userId = resolveUserId(principal);
        if (request.getCustomerId() != null && !request.getCustomerId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized appointment request");
        }
        Appointment appointment = appointmentService.bookAppointmentByPincode(userId, request);
        return ResponseEntity.ok(toCustomerResponse(appointment));
    }

    @GetMapping("/appointments/{customerId}")
    public ResponseEntity<List<CustomerAppointmentResponse>> listAppointments(@PathVariable Long customerId,
                                                                              Principal principal) {
        Long userId = resolveUserId(principal);
        if (!customerId.equals(userId)) {
            throw new IllegalArgumentException("Unauthorized appointment request");
        }
        List<CustomerAppointmentResponse> response = appointmentService.getAppointmentsForCustomer(customerId)
                .stream()
                .map(this::toCustomerResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private Long resolveUserId(Principal principal) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getId();
    }

    private CustomerAppointmentResponse toCustomerResponse(Appointment appointment) {
        CustomerAppointmentResponse response = new CustomerAppointmentResponse();
        response.setId(appointment.getId());
        response.setAgentId(appointment.getAgent().getId());
        response.setAgentName(appointment.getAgent().getFullName());
        LocalDate date = appointment.getAppointmentTime() != null ? appointment.getAppointmentTime().toLocalDate() : null;
        LocalTime time = appointment.getAppointmentTime() != null ? appointment.getAppointmentTime().toLocalTime() : null;
        response.setAppointmentDate(date);
        response.setAppointmentTime(time);
        response.setPincode(appointment.getPincode());
        response.setStatus(normalizeStatus(appointment.getStatus().name()));
        return response;
    }

    private String normalizeStatus(String status) {
        return "SCHEDULED".equalsIgnoreCase(status) ? "PENDING" : status;
    }
}
