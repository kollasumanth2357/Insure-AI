package com.insure.insurebackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "agent_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private Integer experienceYears;

    @Enumerated(EnumType.STRING)
    private AgentSpecialization specialization;

    private String serviceAreas;

    @Enumerated(EnumType.STRING)
    private AvailabilityStatus availabilityStatus;

    private String status = "ACTIVE";

    private String pincode;

    private Double latitude;

    private Double longitude;

    private Double revenueGenerated;

    private Integer totalAppointments;

    private Boolean softDeleted;

    private Boolean isDeleted;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
