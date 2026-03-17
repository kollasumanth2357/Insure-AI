package com.insure.insurebackend.controller;

import com.insure.insurebackend.dto.AppointmentResponse;
import com.insure.insurebackend.dto.AvailabilityUpdateRequest;
import com.insure.insurebackend.dto.FeedbackRequest;
import com.insure.insurebackend.dto.PolicyResponse;
import com.insure.insurebackend.model.AgentProfile;
import com.insure.insurebackend.model.Appointment;
import com.insure.insurebackend.model.ContactMessage;
import com.insure.insurebackend.model.User;
import com.insure.insurebackend.repository.ContactMessageRepository;
import com.insure.insurebackend.service.AgentProfileService;
import com.insure.insurebackend.service.AppointmentService;
import com.insure.insurebackend.service.AuditLogService;
import com.insure.insurebackend.service.PolicyService;
import com.insure.insurebackend.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/agent")
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class AgentController {

    private final PolicyService policyService;
    private final AppointmentService appointmentService;
    private final AgentProfileService agentProfileService;
    private final ContactMessageRepository contactMessageRepository;
    private final AuditLogService auditLogService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public AgentController(PolicyService policyService,
                           AppointmentService appointmentService,
                           AgentProfileService agentProfileService,
                           ContactMessageRepository contactMessageRepository,
                           AuditLogService auditLogService,
                           UserService userService,
                           ModelMapper modelMapper) {
        this.policyService = policyService;
        this.appointmentService = appointmentService;
        this.agentProfileService = agentProfileService;
        this.contactMessageRepository = contactMessageRepository;
        this.auditLogService = auditLogService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/policies")
    public ResponseEntity<List<PolicyResponse>> getPolicies() {
        List<PolicyResponse> response = policyService.getActivePolicies()
                .stream()
                .map(policy -> modelMapper.map(policy, PolicyResponse.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponse>> getAppointments(Principal principal) {
        Long agentId = resolveUserId(principal);
        List<AppointmentResponse> response = appointmentService.getAppointmentsForAgent(agentId)
                .stream()
                .map(this::toAppointmentResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/appointments/{id}/start")
    public ResponseEntity<AppointmentResponse> startAppointment(@PathVariable Long id, Principal principal) {
        Appointment appointment = appointmentService.startAppointment(id, resolveUserId(principal));
        return ResponseEntity.ok(toAppointmentResponse(appointment));
    }

    @PutMapping("/appointments/{id}/complete")
    public ResponseEntity<AppointmentResponse> completeAppointment(@PathVariable Long id, Principal principal) {
        Appointment appointment = appointmentService.completeAppointment(id, resolveUserId(principal));
        return ResponseEntity.ok(toAppointmentResponse(appointment));
    }

    @PatchMapping("/availability")
    public ResponseEntity<?> updateAvailability(@Valid @RequestBody AvailabilityUpdateRequest request,
                                                Principal principal) {
        AgentProfile profile = agentProfileService.updateAvailability(resolveUserId(principal), request.getAvailabilityStatus());
        log.info("Agent {} updated availability to {}", principal.getName(), request.getAvailabilityStatus());
        auditLogService.log(resolveUserId(principal), "UPDATE_AVAILABILITY", "AGENT_PROFILE", profile.getId(),
                "Availability set to " + request.getAvailabilityStatus());
        return ResponseEntity.ok(java.util.Map.of(
                "availabilityStatus", profile.getAvailabilityStatus().name()
        ));
    }

    @GetMapping("/revenue")
    public ResponseEntity<?> revenue(Principal principal) {
        AgentProfile profile = agentProfileService.getByUserId(resolveUserId(principal));
        return ResponseEntity.ok(java.util.Map.of(
                "revenueGenerated", profile.getRevenueGenerated() == null ? 0.0 : profile.getRevenueGenerated()
        ));
    }

    @GetMapping("/performance")
    public ResponseEntity<?> performance(Principal principal) {
        AgentProfile profile = agentProfileService.getByUserId(resolveUserId(principal));
        int totalAppointments = profile.getTotalAppointments() == null ? 0 : profile.getTotalAppointments();
        double score = totalAppointments == 0 ? 0.0 : Math.min(100.0, totalAppointments * 5.0);
        return ResponseEntity.ok(java.util.Map.of(
                "performanceScore", score,
                "totalAppointments", totalAppointments
        ));
    }

    @PostMapping("/feedback")
    public ResponseEntity<?> submitFeedback(@Valid @RequestBody FeedbackRequest request,
                                            Principal principal) {
        User agent = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Agent not found"));

        ContactMessage message = new ContactMessage();
        message.setName(agent.getFullName());
        message.setEmail(agent.getEmail());
        message.setSubject("AGENT_FEEDBACK");
        message.setMessage(request.getMessage());
        contactMessageRepository.save(message);

        log.info("Agent {} submitted feedback", agent.getUsername());
        auditLogService.log(agent.getId(), "AGENT_FEEDBACK", "CONTACT_MESSAGE", message.getId(),
                "Feedback submitted");

        return ResponseEntity.ok(java.util.Map.of("message", "Feedback received"));
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
        response.setAgentSpecialization(null);
        if (appointment.getPolicy() != null) {
            response.setPolicyId(appointment.getPolicy().getId());
            response.setPolicyName(appointment.getPolicy().getName());
            response.setPolicyType(resolvePolicyCategory(appointment.getPolicy().getMainCategory()));
        } else {
            response.setPolicyId(null);
            response.setPolicyName("N/A");
            response.setPolicyType("N/A");
        }
        response.setAppointmentTime(appointment.getAppointmentTime());
        response.setPincode(appointment.getPincode());
        response.setStatus(normalizeStatus(appointment.getStatus().name()));
        response.setNotes(appointment.getNotes());
        response.setCustomerLatitude(appointment.getCustomerLatitude());
        response.setCustomerLongitude(appointment.getCustomerLongitude());
        return response;
    }

    private String resolvePolicyCategory(String mainCategory) {
        return mainCategory == null || mainCategory.isBlank() ? "UNSPECIFIED" : mainCategory;
    }

    private Long resolveUserId(Principal principal) {
        return userService.findByUsername(principal.getName())
                .map(User::getId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private String normalizeStatus(String status) {
        return "SCHEDULED".equalsIgnoreCase(status) ? "PENDING" : status;
    }

    private String resolveCustomerField(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
