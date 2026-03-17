package com.insure.insurebackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AgentCreateRequest {
    @NotBlank
    private String fullName;
    @NotBlank
    private String username;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String phone;
    @Size(min = 8)
    private String password;
    @NotNull
    private Integer experienceYears;
    @NotBlank
    private String specialization;
    private String serviceAreas;
    private String availabilityStatus;
    private String pincode;
    private Double latitude;
    private Double longitude;
    private AddressDto address;
}
