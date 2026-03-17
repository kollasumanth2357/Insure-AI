package com.insure.insurebackend.repository;

import com.insure.insurebackend.model.Appointment;
import com.insure.insurebackend.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByAgentId(Long agentId);

    List<Appointment> findByCustomerId(Long customerId);

    long countByStatus(AppointmentStatus status);
}
