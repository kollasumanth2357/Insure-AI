package com.insure.insurebackend.dto;

import com.insure.insurebackend.model.AgentSpecialization;

public class AgentDirectoryResponse {
    private final Long userId;
    private final Long profileId;
    private final String fullName;
    private final String username;
    private final String email;
    private final String phone;
    private final AgentSpecialization specialization;
    private final String status;

    public AgentDirectoryResponse(Long userId,
                                  Long profileId,
                                  String fullName,
                                  String username,
                                  String email,
                                  String phone,
                                  AgentSpecialization specialization,
                                  String status) {
        this.userId = userId;
        this.profileId = profileId;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.specialization = specialization;
        this.status = status;
    }

    public Long getUserId() { return userId; }
    public Long getProfileId() { return profileId; }
    public String getFullName() { return fullName; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public AgentSpecialization getSpecialization() { return specialization; }
    public String getStatus() { return status; }
}
