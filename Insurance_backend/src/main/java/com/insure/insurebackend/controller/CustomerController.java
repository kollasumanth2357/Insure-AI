package com.insure.insurebackend.controller;

import com.insure.insurebackend.dto.*;
import com.insure.insurebackend.model.Appointment;
import com.insure.insurebackend.model.CustomerPolicy;
import com.insure.insurebackend.model.User;
import com.insure.insurebackend.repository.AgentProfileRepository;
import com.insure.insurebackend.service.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class CustomerController {

    private final UserService userService;
    private final PolicyService policyService;
    private final CustomerPolicyService customerPolicyService;
    private final AppointmentService appointmentService;
    private final PaymentService paymentService;
    private final AuditLogService auditLogService;
    private final AgentProfileRepository agentProfileRepository;
    private final ModelMapper modelMapper;

    public CustomerController(UserService userService,
                              PolicyService policyService,
                              CustomerPolicyService customerPolicyService,
                              AppointmentService appointmentService,
                              PaymentService paymentService,
                              AuditLogService auditLogService,
                              AgentProfileRepository agentProfileRepository,
                              ModelMapper modelMapper) {
        this.userService = userService;
        this.policyService = policyService;
        this.customerPolicyService = customerPolicyService;
        this.appointmentService = appointmentService;
        this.paymentService = paymentService;
        this.auditLogService = auditLogService;
        this.agentProfileRepository = agentProfileRepository;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody CustomerRegisterRequest request) {
        User user = new User();
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword());
        userService.register(user);
        log.info("Customer registered {}", request.getUsername());
        return ResponseEntity.ok(java.util.Map.of("message", "Registered Successfully"));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<String> dashboard() {
        return ResponseEntity.ok("Welcome Customer Dashboard");
    }

    @GetMapping("/policies")
    public ResponseEntity<List<PolicyResponse>> viewPolicies(Principal principal) {
        Long userId = resolveUserId(principal);
        auditLogService.log(userId, "POLICY_VIEW", "POLICY", null, "Customer viewed policies");
        List<PolicyResponse> response = policyService.getActivePolicies()
                .stream()
                .map(policy -> modelMapper.map(policy, PolicyResponse.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/policies/purchase")
    public ResponseEntity<CustomerPolicyResponse> purchasePolicy(@Valid @RequestBody CustomerPolicyRequest request,
                                                                 Principal principal) {
        Long userId = resolveUserId(principal);
        CustomerPolicy customerPolicy = customerPolicyService.purchasePolicy(userId, request);
        log.info("Customer {} purchased policy {}", principal.getName(), request.getPolicyId());
        auditLogService.log(userId, "PURCHASE_POLICY", "CUSTOMER_POLICY", customerPolicy.getId(),
                "Policy purchased");
        return ResponseEntity.ok(toCustomerPolicyResponse(customerPolicy));
    }

    @GetMapping("/policies/purchased")
    public ResponseEntity<List<CustomerPolicyResponse>> purchasedPolicies(Principal principal) {
        Long userId = resolveUserId(principal);
        List<CustomerPolicyResponse> response = customerPolicyService.getCustomerPolicies(userId)
                .stream()
                .map(this::toCustomerPolicyResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/appointments")
    public ResponseEntity<AppointmentResponse> bookAppointment(@Valid @RequestBody AppointmentRequest request,
                                                               Principal principal) {
        Long userId = resolveUserId(principal);
        Appointment appointment = appointmentService.bookAppointment(userId, request);
        log.info("Customer {} booked appointment {}", principal.getName(), appointment.getId());
        auditLogService.log(userId, "BOOK_APPOINTMENT", "APPOINTMENT", appointment.getId(),
                "Appointment booked");
        return ResponseEntity.ok(toAppointmentResponse(appointment));
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponse>> myAppointments(Principal principal) {
        Long userId = resolveUserId(principal);
        List<AppointmentResponse> response = appointmentService.getAppointmentsForCustomer(userId)
                .stream()
                .map(this::toAppointmentResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentResponse>> paymentHistory(Principal principal) {
        Long userId = resolveUserId(principal);
        List<PaymentResponse> response = paymentService.getPaymentsForCustomer(userId)
                .stream()
                .map(payment -> {
                    PaymentResponse res = new PaymentResponse();
                    res.setId(payment.getId());
                    res.setPolicyId(payment.getPolicy().getId());
                    res.setAmount(payment.getAmount());
                    res.setStatus(payment.getStatus().name());
                    res.setPaidAt(payment.getPaidAt());
                    return res;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private Long resolveUserId(Principal principal) {
        return userService.findByUsername(principal.getName())
                .map(User::getId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
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

    private CustomerPolicyResponse toCustomerPolicyResponse(CustomerPolicy customerPolicy) {
        CustomerPolicyResponse response = new CustomerPolicyResponse();
        response.setId(customerPolicy.getId());
        response.setPolicyId(customerPolicy.getPolicy().getId());
        response.setPolicyName(customerPolicy.getPolicy().getName());
        response.setPolicyType(resolvePolicyCategory(customerPolicy.getPolicy().getMainCategory()));
        response.setStatus(customerPolicy.getStatus().name());
        response.setPurchaseDate(customerPolicy.getPurchaseDate());
        response.setAgentId(customerPolicy.getAgent() != null ? customerPolicy.getAgent().getId() : null);
        return response;
    }

    private String resolvePolicyCategory(String mainCategory) {
        return mainCategory == null || mainCategory.isBlank() ? "UNSPECIFIED" : mainCategory;
    }

    private String formatSpecialization(String specialization) {
        return specialization.replace("_", " ");
    }

    private String normalizeStatus(String status) {
        return "SCHEDULED".equalsIgnoreCase(status) ? "PENDING" : status;
    }

    private String resolveCustomerField(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
