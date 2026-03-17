package com.insure.insurebackend.service;

import com.insure.insurebackend.dto.AdminDashboardStats;
import com.insure.insurebackend.model.AppointmentStatus;
import com.insure.insurebackend.model.Role;
import com.insure.insurebackend.repository.AppointmentRepository;
import com.insure.insurebackend.repository.PolicyRepository;
import com.insure.insurebackend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final AppointmentRepository appointmentRepository;

    public AdminService(UserRepository userRepository,
                        PolicyRepository policyRepository,
                        AppointmentRepository appointmentRepository) {
        this.userRepository = userRepository;
        this.policyRepository = policyRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public AdminDashboardStats getDashboardStats() {
        AdminDashboardStats stats = new AdminDashboardStats();
        stats.setTotalCustomers(userRepository.countByRole(Role.CUSTOMER));
        stats.setTotalAgents(userRepository.countByRole(Role.AGENT));
        stats.setActivePolicies(policyRepository.countActivePolicies());
        stats.setTotalAppointments(appointmentRepository.count());
        stats.setCompletedAppointments(appointmentRepository.countByStatus(AppointmentStatus.COMPLETED));
        stats.setPendingAppointments(appointmentRepository.countByStatus(AppointmentStatus.PENDING));
        return stats;
    }
}
