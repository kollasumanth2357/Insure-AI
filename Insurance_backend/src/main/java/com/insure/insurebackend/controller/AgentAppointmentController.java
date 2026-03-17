package com.insure.insurebackend.controller;

import com.insure.insurebackend.dto.AgentAppointmentResponse;
import com.insure.insurebackend.dto.AppointmentStatusUpdateRequest;
import com.insure.insurebackend.model.Appointment;
import com.insure.insurebackend.model.AppointmentStatus;
import com.insure.insurebackend.model.User;
import com.insure.insurebackend.service.AppointmentService;
import com.insure.insurebackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/agent")
@CrossOrigin(origins = "http://localhost:5173")
public class AgentAppointmentController {

    private static final Set<AppointmentStatus> ALLOWED_STATUSES =
            Set.of(AppointmentStatus.CONFIRMED, AppointmentStatus.COMPLETED, AppointmentStatus.CANCELLED);

    private final AppointmentService appointmentService;
    private final UserService userService;

    public AgentAppointmentController(AppointmentService appointmentService,
                                      UserService userService) {
        this.appointmentService = appointmentService;
        this.userService = userService;
    }

    @GetMapping("/appointments/{agentId}")
    public ResponseEntity<List<AgentAppointmentResponse>> listAgentAppointments(@PathVariable Long agentId,
                                                                                Principal principal) {
        Long userId = resolveUserId(principal);
        if (!agentId.equals(userId)) {
            throw new IllegalArgumentException("Unauthorized appointment request");
        }
        List<AgentAppointmentResponse> response = appointmentService.getAppointmentsForAgent(agentId)
                .stream()
                .map(this::toAgentResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/appointments/{appointmentId}/status")
    public ResponseEntity<AgentAppointmentResponse> updateStatus(@PathVariable Long appointmentId,
                                                                 @Valid @RequestBody AppointmentStatusUpdateRequest request,
                                                                 Principal principal) {
        Long agentId = resolveUserId(principal);
        AppointmentStatus nextStatus = AppointmentStatus.valueOf(request.getStatus().toUpperCase());
        if (!ALLOWED_STATUSES.contains(nextStatus)) {
            throw new IllegalArgumentException("Invalid status update");
        }
        Appointment appointment = appointmentService.updateAppointmentStatus(appointmentId, agentId, nextStatus);
        return ResponseEntity.ok(toAgentResponse(appointment));
    }

    private Long resolveUserId(Principal principal) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getId();
    }

    private AgentAppointmentResponse toAgentResponse(Appointment appointment) {
        AgentAppointmentResponse response = new AgentAppointmentResponse();
        response.setId(appointment.getId());
        response.setCustomerId(appointment.getCustomer().getId());
        response.setCustomerName(appointment.getCustomerName());
        response.setCustomerEmail(appointment.getCustomerEmail());
        response.setCustomerPhone(appointment.getCustomerPhone());
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
