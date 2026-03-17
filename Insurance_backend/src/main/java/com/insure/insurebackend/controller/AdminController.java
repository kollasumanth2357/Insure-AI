package com.insure.insurebackend.controller;

import com.insure.insurebackend.dto.*;
import com.insure.insurebackend.model.AgentProfile;
import com.insure.insurebackend.model.Appointment;
import com.insure.insurebackend.model.Policy;
import com.insure.insurebackend.repository.AuditLogRepository;
import com.insure.insurebackend.repository.AgentProfileRepository;
import com.insure.insurebackend.repository.PolicyRepository;
import com.insure.insurebackend.service.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class AdminController {

    private final AdminAgentService adminAgentService;
    private final AppointmentService appointmentService;
    private final PolicyService policyService;
    private final PolicyRepository policyRepository;
    private final AnalyticsService analyticsService;
    private final AdminService adminService;
    private final AuditLogService auditLogService;
    private final AuditLogRepository auditLogRepository;
    private final UserService userService;
    private final AgentProfileRepository agentProfileRepository;
    private final ModelMapper modelMapper;

    public AdminController(AdminAgentService adminAgentService,
                           AppointmentService appointmentService,
                           PolicyService policyService,
                           AnalyticsService analyticsService,
                           AdminService adminService,
                           AuditLogService auditLogService,
                           AuditLogRepository auditLogRepository,
                           UserService userService,
                           AgentProfileRepository agentProfileRepository,
                           PolicyRepository policyRepository,
                           ModelMapper modelMapper) {
        this.adminAgentService = adminAgentService;
        this.appointmentService = appointmentService;
        this.policyService = policyService;
        this.analyticsService = analyticsService;
        this.adminService = adminService;
        this.auditLogService = auditLogService;
        this.auditLogRepository = auditLogRepository;
        this.userService = userService;
        this.agentProfileRepository = agentProfileRepository;
        this.policyRepository = policyRepository;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/agents")
    public ResponseEntity<AgentResponse> createAgent(@Valid @RequestBody AgentCreateRequest request,
                                                     Principal principal) {
        AgentProfile profile = adminAgentService.createAgent(request);
        log.info("Admin created agent {}", profile.getUser().getUsername());
        auditLogService.log(resolveUserId(principal), "CREATE_AGENT", "AGENT_PROFILE", profile.getId(),
                "Agent created: " + profile.getUser().getUsername());
        return ResponseEntity.ok(toAgentResponse(profile));
    }

    @GetMapping("/agents")
    public ResponseEntity<List<AgentResponse>> listAgents() {
        List<AgentResponse> response = adminAgentService.getAllAgents()
                .stream()
                .map(this::toAgentResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/agents/{id}")
    public ResponseEntity<AgentResponse> updateAgent(@PathVariable Long id,
                                                     @RequestBody AgentUpdateRequest request,
                                                     Principal principal) {
        AgentProfile profile = adminAgentService.updateAgent(id, request);
        log.info("Admin updated agent {}", profile.getUser().getUsername());
        auditLogService.log(resolveUserId(principal), "UPDATE_AGENT", "AGENT_PROFILE", profile.getId(),
                "Agent updated: " + profile.getUser().getUsername());
        return ResponseEntity.ok(toAgentResponse(profile));
    }

    @PatchMapping("/agents/{id}/soft-delete")
    public ResponseEntity<AgentResponse> softDeleteAgent(@PathVariable Long id, Principal principal) {
        AgentProfile profile = adminAgentService.softDeleteAgent(id);
        log.info("Admin soft deleted agent {}", profile.getUser().getUsername());
        auditLogService.log(resolveUserId(principal), "SOFT_DELETE_AGENT", "AGENT_PROFILE", profile.getId(),
                "Agent soft deleted: " + profile.getUser().getUsername());
        return ResponseEntity.ok(toAgentResponse(profile));
    }

    @PatchMapping("/agents/{id}/status")
    public ResponseEntity<AgentResponse> setAgentStatus(@PathVariable Long id,
                                                        @RequestParam boolean active,
                                                        Principal principal) {
        AgentProfile profile = adminAgentService.setAgentStatus(id, active);
        log.info("Admin set agent status {} to {}", profile.getUser().getUsername(), active);
        auditLogService.log(resolveUserId(principal), "SET_AGENT_STATUS", "AGENT_PROFILE", profile.getId(),
                "Agent status changed to " + (active ? "ACTIVE" : "INACTIVE"));
        return ResponseEntity.ok(toAgentResponse(profile));
    }

    @PutMapping("/agents/{id}/activate")
    public ResponseEntity<AgentResponse> activateAgent(@PathVariable Long id, Principal principal) {
        AgentProfile profile = adminAgentService.activateAgent(id);
        log.info("Admin activated agent {}", profile.getUser().getUsername());
        auditLogService.log(resolveUserId(principal), "ACTIVATE_AGENT", "AGENT_PROFILE", profile.getId(),
                "Agent activated: " + profile.getUser().getUsername());
        return ResponseEntity.ok(toAgentResponse(profile));
    }

    @PutMapping("/agents/{id}/deactivate")
    public ResponseEntity<AgentResponse> deactivateAgent(@PathVariable Long id, Principal principal) {
        AgentProfile profile = adminAgentService.deactivateAgent(id);
        log.info("Admin deactivated agent {}", profile.getUser().getUsername());
        auditLogService.log(resolveUserId(principal), "DEACTIVATE_AGENT", "AGENT_PROFILE", profile.getId(),
                "Agent deactivated: " + profile.getUser().getUsername());
        return ResponseEntity.ok(toAgentResponse(profile));
    }

    @PostMapping("/policies")
    public ResponseEntity<PolicyResponse> createPolicy(@Valid @RequestBody PolicyRequest request,
                                                       Principal principal) {
        Policy policy = policyService.createPolicy(request);
        log.info("Admin created policy {}", policy.getName());
        auditLogService.log(resolveUserId(principal), "CREATE_POLICY", "POLICY", policy.getId(),
                "Policy created: " + policy.getName());
        return ResponseEntity.ok(modelMapper.map(policy, PolicyResponse.class));
    }

    @PutMapping("/policies/{id}")
    public ResponseEntity<PolicyResponse> updatePolicy(@PathVariable Long id,
                                                       @Valid @RequestBody PolicyRequest request,
                                                       Principal principal) {
        Policy policy = policyService.updatePolicy(id, request);
        log.info("Admin updated policy {}", policy.getName());
        auditLogService.log(resolveUserId(principal), "UPDATE_POLICY", "POLICY", policy.getId(),
                "Policy updated: " + policy.getName());
        return ResponseEntity.ok(modelMapper.map(policy, PolicyResponse.class));
    }

    @GetMapping("/policies")
    public ResponseEntity<List<PolicyResponse>> listPolicies() {
        List<PolicyResponse> response = policyService.getAllPolicies().stream()
                .map(policy -> modelMapper.map(policy, PolicyResponse.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/policies/{id}/toggle-status")
    public ResponseEntity<?> togglePolicyStatus(@PathVariable Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        boolean nextActive = policy.getActive() == null || !policy.getActive();
        policy.setActive(nextActive);
        policy.setStatus(nextActive ? "ACTIVE" : "INACTIVE");
        policyRepository.save(policy);
        return ResponseEntity.ok("Policy status updated");
    }

    @GetMapping("/dashboard/kpis")
    public ResponseEntity<DashboardKpiResponse> dashboardKpis() {
        return ResponseEntity.ok(analyticsService.dashboardKpis());
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<AdminDashboardStats> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/revenue-reports")
    public ResponseEntity<List<RevenueReportResponse>> revenueReports(@RequestParam(defaultValue = "6") int months) {
        return ResponseEntity.ok(analyticsService.revenueReports(months));
    }

    @GetMapping("/policy-distribution")
    public ResponseEntity<List<PolicyDistributionResponse>> policyDistribution() {
        return ResponseEntity.ok(analyticsService.policyDistribution());
    }

    @GetMapping("/appointments/analytics")
    public ResponseEntity<AppointmentAnalyticsResponse> appointmentAnalytics() {
        return ResponseEntity.ok(analyticsService.appointmentAnalytics());
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponse>> listAppointments() {
        List<AppointmentResponse> response = appointmentService.getAllAppointments()
                .stream()
                .map(this::toAppointmentResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/payments/summary")
    public ResponseEntity<PaymentSummaryResponse> paymentSummary() {
        return ResponseEntity.ok(analyticsService.paymentSummary());
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<Page<AuditLogResponse>> auditLogs(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "20") int size) {
        Page<AuditLogResponse> response = auditLogRepository.findAll(PageRequest.of(page, size))
                .map(log -> modelMapper.map(log, AuditLogResponse.class));
        return ResponseEntity.ok(response);
    }

    private AgentResponse toAgentResponse(AgentProfile profile) {
        AgentResponse response = new AgentResponse();
        response.setId(profile.getId());
        response.setUserId(profile.getUser().getId());
        response.setFullName(profile.getUser().getFullName());
        response.setUsername(profile.getUser().getUsername());
        response.setEmail(profile.getUser().getEmail());
        response.setPhone(profile.getUser().getPhone());
        response.setExperienceYears(profile.getExperienceYears());
        response.setSpecialization(profile.getSpecialization().name());
        response.setServiceAreas(profile.getServiceAreas());
        response.setAvailabilityStatus(profile.getAvailabilityStatus().name());
        response.setStatus(profile.getStatus());
        response.setPincode(profile.getPincode());
        response.setLatitude(profile.getLatitude());
        response.setLongitude(profile.getLongitude());
        response.setRevenueGenerated(profile.getRevenueGenerated());
        response.setTotalAppointments(profile.getTotalAppointments());
        response.setSoftDeleted(profile.getSoftDeleted());
        return response;
    }

    private AgentResponse toAgentResponse(AgentDirectoryResponse agent) {
        AgentResponse response = new AgentResponse();
        response.setId(agent.getProfileId());
        response.setUserId(agent.getUserId());
        response.setFullName(agent.getFullName());
        response.setUsername(agent.getUsername());
        response.setEmail(agent.getEmail());
        response.setPhone(agent.getPhone());
        response.setSpecialization(agent.getSpecialization() != null
                ? agent.getSpecialization().name()
                : null);
        response.setStatus(agent.getStatus());
        return response;
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

    private String normalizeStatus(String status) {
        return "SCHEDULED".equalsIgnoreCase(status) ? "PENDING" : status;
    }

    private String formatSpecialization(String specialization) {
        return specialization.replace("_", " ");
    }

    private String resolveCustomerField(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String resolvePolicyCategory(String type) {

        if (type == null) {
            return "GENERAL";
        }

        switch (type.toUpperCase()) {

            case "HEALTH":
                return "Health Insurance";

            case "CAR":
            case "BIKE":
            case "VEHICLE":
                return "Vehicle Insurance";

            case "HOME":
                return "Home Insurance";

            case "BUSINESS":
                return "Business Insurance";

            case "LIFE":
                return "Life Insurance";

            case "ONLINE":
                return "Online Insurance";

            default:
                return "General Insurance";
        }
    }

    private Long resolveUserId(Principal principal) {
        if (principal == null) {
            return null;
        }
        return userService.findByUsername(principal.getName())
                .map(user -> user.getId())
                .orElse(null);
    }
}
