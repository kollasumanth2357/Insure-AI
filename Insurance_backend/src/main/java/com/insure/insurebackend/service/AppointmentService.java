package com.insure.insurebackend.service;

import com.insure.insurebackend.dto.AppointmentRequest;
import com.insure.insurebackend.dto.CustomerAppointmentRequest;
import com.insure.insurebackend.model.Appointment;
import com.insure.insurebackend.model.AppointmentStatus;
import com.insure.insurebackend.model.Policy;
import com.insure.insurebackend.model.Role;
import com.insure.insurebackend.model.User;
import com.insure.insurebackend.repository.AgentProfileRepository;
import com.insure.insurebackend.repository.AppointmentRepository;
import com.insure.insurebackend.repository.PolicyRepository;
import com.insure.insurebackend.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final AgentProfileRepository agentProfileRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              UserRepository userRepository,
                              PolicyRepository policyRepository,
                              AgentProfileRepository agentProfileRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.policyRepository = policyRepository;
        this.agentProfileRepository = agentProfileRepository;
    }

    @Transactional
    public Appointment bookAppointment(Long customerId, AppointmentRequest request) {

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        if (customer.getRole() != Role.CUSTOMER) {
            throw new IllegalArgumentException("Only customers can book appointments");
        }

        User agent = null;
        if (request.getAgentId() != null) {
            agent = userRepository.findById(request.getAgentId())
                    .orElseThrow(() -> new IllegalArgumentException("Agent not found"));
        } else {
            agent = findNearestAgent(request.getCustomerLatitude(), request.getCustomerLongitude());
        }

        if (agent == null || agent.getRole() != Role.AGENT) {
            throw new IllegalArgumentException("Selected user is not an agent");
        }

        Policy policy = null;
        if (request.getPolicyId() != null) {
            policy = policyRepository.findById(request.getPolicyId())
                    .orElseThrow(() -> new IllegalArgumentException("Policy not found"));
            if (policy.getIsDeleted() != null && policy.getIsDeleted()) {
                throw new IllegalArgumentException("Policy not found");
            }
            if (policy.getStatus() != null && !"ACTIVE".equalsIgnoreCase(policy.getStatus())) {
                throw new IllegalArgumentException("Policy not found");
            }
        }

        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setAgent(agent);
        appointment.setPolicy(policy);
        LocalDateTime appointmentTime = request.getAppointmentDate() != null
                ? request.getAppointmentDate()
                : request.getAppointmentTime();
        if (appointmentTime == null) {
            throw new IllegalArgumentException("Appointment date is required");
        }
        appointment.setAppointmentTime(appointmentTime);
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setPincode(request.getPincode());
        appointment.setNotes(request.getNotes());
        appointment.setCustomerName(resolveCustomerField(request.getCustomerName(), customer.getFullName()));
        appointment.setCustomerEmail(resolveCustomerField(request.getCustomerEmail(), customer.getEmail()));
        appointment.setCustomerPhone(resolveCustomerField(request.getCustomerPhone(), customer.getPhone()));
        appointment.setCustomerLatitude(request.getCustomerLatitude());
        appointment.setCustomerLongitude(request.getCustomerLongitude());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        agentProfileRepository.findByUserId(agent.getId()).ifPresent(profile -> {
            Integer total = profile.getTotalAppointments();
            profile.setTotalAppointments(total == null ? 1 : total + 1);
            agentProfileRepository.save(profile);
        });

        return savedAppointment;
    }

    @Transactional
    public Appointment bookAppointmentByPincode(Long customerId, CustomerAppointmentRequest request) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        if (customer.getRole() != Role.CUSTOMER) {
            throw new IllegalArgumentException("Only customers can book appointments");
        }

        if (request.getAgentId() == null) {
            throw new IllegalArgumentException("Agent selection is required");
        }

        User agent = userRepository.findById(request.getAgentId())
                .orElseThrow(() -> new IllegalArgumentException("Agent not found"));

        if (agent.getRole() != Role.AGENT) {
            throw new IllegalArgumentException("Selected user is not an agent");
        }

        if (request.getAppointmentDate() == null || request.getAppointmentTime() == null) {
            throw new IllegalArgumentException("Appointment date and time are required");
        }

        LocalDateTime appointmentDateTime = LocalDateTime.of(
                request.getAppointmentDate(),
                request.getAppointmentTime() == null ? LocalTime.of(9, 0) : request.getAppointmentTime()
        );

        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setAgent(agent);
        appointment.setPolicy(null);
        appointment.setAppointmentTime(appointmentDateTime);
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setPincode(request.getPincode());
        appointment.setNotes(request.getNotes());
        appointment.setCustomerName(resolveCustomerField(request.getCustomerName(), customer.getFullName()));
        appointment.setCustomerEmail(resolveCustomerField(request.getCustomerEmail(), customer.getEmail()));
        appointment.setCustomerPhone(resolveCustomerField(request.getCustomerPhone(), customer.getPhone()));
        appointment.setCustomerLatitude(request.getCustomerLatitude());
        appointment.setCustomerLongitude(request.getCustomerLongitude());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        agentProfileRepository.findByUserId(agent.getId()).ifPresent(profile -> {
            Integer total = profile.getTotalAppointments();
            profile.setTotalAppointments(total == null ? 1 : total + 1);
            agentProfileRepository.save(profile);
        });

        return savedAppointment;
    }

    public List<Appointment> getAppointmentsForAgent(Long agentId) {
        return appointmentRepository.findByAgentId(agentId);
    }

    public List<Appointment> getAppointmentsForCustomer(Long customerId) {
        return appointmentRepository.findByCustomerId(customerId);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Transactional
    public Appointment updateAppointmentStatus(Long appointmentId, Long agentId, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        if (!appointment.getAgent().getId().equals(agentId)) {
            throw new IllegalArgumentException("Unauthorized appointment access");
        }
        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment startAppointment(Long appointmentId, Long agentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        if (!appointment.getAgent().getId().equals(agentId)) {
            throw new IllegalArgumentException("Unauthorized appointment access");
        }
        appointment.setStatus(AppointmentStatus.IN_PROGRESS);
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment completeAppointment(Long appointmentId, Long agentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
        if (!appointment.getAgent().getId().equals(agentId)) {
            throw new IllegalArgumentException("Unauthorized appointment access");
        }
        appointment.setStatus(AppointmentStatus.COMPLETED);
        return appointmentRepository.save(appointment);
    }

    private User findNearestAgent(Double customerLat, Double customerLng) {
        if (customerLat == null || customerLng == null) {
            throw new IllegalArgumentException("Customer location is required to auto-assign an agent");
        }
        return agentProfileRepository.findAll()
                .stream()
                .filter(profile -> profile.getSoftDeleted() == null || !profile.getSoftDeleted())
                .filter(profile -> profile.getIsDeleted() == null || !profile.getIsDeleted())
                .filter(profile -> profile.getStatus() == null || "ACTIVE".equalsIgnoreCase(profile.getStatus()))
                .filter(profile -> profile.getLatitude() != null && profile.getLongitude() != null)
                .map(profile -> new AgentDistance(profile.getUser(), distanceKm(customerLat, customerLng,
                        profile.getLatitude(), profile.getLongitude())))
                .min((a, b) -> Double.compare(a.distance, b.distance))
                .map(agentDistance -> agentDistance.agent)
                .orElseThrow(() -> new IllegalArgumentException("No active agents available"));
    }

    private double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371.0;
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double diffLng = Math.toRadians(lon2 - lon1);
        return earthRadius * Math.acos(
                Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.cos(diffLng)
                        - Math.sin(lat1Rad) * Math.sin(lat2Rad)
        );
    }

    private String resolveCustomerField(String requested, String fallback) {
        return requested == null || requested.isBlank() ? fallback : requested;
    }

    private static class AgentDistance {
        private final User agent;
        private final double distance;

        private AgentDistance(User agent, double distance) {
            this.agent = agent;
            this.distance = distance;
        }
    }
}
